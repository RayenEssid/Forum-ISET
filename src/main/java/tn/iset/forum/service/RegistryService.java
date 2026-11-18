package tn.iset.forum.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.iset.forum.entity.Role;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class RegistryService {

    private final Set<String> etudiantIds;
    private final Set<String> enseignantIds;

    public RegistryService(@Value("${app.registry.dir}") String directory) throws IOException {
        Path dir = Paths.get(directory).toAbsolutePath().normalize();

        etudiantIds = readColumn(dir.resolve("Registre_Etudiants_Complet.xlsx"), "inscription");
        enseignantIds = readColumn(dir.resolve("Enseignants.xlsx"), "codeens");

        System.out.println(">>> Registres charges : " + etudiantIds.size()
                + " etudiants, " + enseignantIds.size() + " enseignants (dossier " + dir + ")");
    }

    private Set<String> readColumn(Path file, String keyword) throws IOException {
        if (!Files.isRegularFile(file)) {
            throw new IOException("Fichier Excel introuvable : " + file
                    + "  -> copie les 2 fichiers .xlsx dans le dossier private-registry");
        }

        Set<String> ids = new HashSet<>();
        DataFormatter formatter = new DataFormatter();

        try (InputStream in = Files.newInputStream(file);
             Workbook workbook = WorkbookFactory.create(in)) {

            for (Sheet sheet : workbook) {
                Row header = sheet.getRow(sheet.getFirstRowNum());
                if (header == null) continue;

                int column = -1;
                for (Cell cell : header) {
                    if (simplify(formatter.formatCellValue(cell)).contains(keyword)) {
                        column = cell.getColumnIndex();
                        break;
                    }
                }
                if (column == -1) continue;

                for (int i = header.getRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;
                    Cell cell = row.getCell(column);
                    if (cell == null) continue;

                    String id = normalize(formatter.formatCellValue(cell));
                    if (!id.isBlank()) ids.add(id);
                }
                break;
            }
        }

        if (ids.isEmpty()) {
            throw new IllegalStateException("Aucun identifiant lu dans " + file.getFileName());
        }
        return Set.copyOf(ids);
    }

    private static String simplify(String title) {
        return title.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}]", "");
    }

    public static String normalize(String value) {
        if (value == null) return "";
        String s = value.strip().toUpperCase(Locale.ROOT);
        if (s.matches("\\d+\\.0+")) {
            s = s.substring(0, s.indexOf('.'));
        }
        return s;
    }

    public boolean isEligible(Role role, String registryId) {
        String id = normalize(registryId);
        return switch (role) {
            case ETUDIANT -> etudiantIds.contains(id);
            case ENSEIGNANT -> enseignantIds.contains(id);
            case ADMIN -> false;
        };
    }
}
