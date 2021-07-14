package de.skuzzle.enforcer.restrictimports.parser.lang;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import de.skuzzle.enforcer.restrictimports.parser.ImportStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JavaLanguageSupport implements LanguageSupport {

    private static final String IMPORT_STATEMENT = "import ";
    private static final String PACKAGE_STATEMENT = "package ";

    private static String ID_PATTERN = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
    private static Pattern FQCN = Pattern.compile(ID_PATTERN + "(\\." + ID_PATTERN + ")*");

    @Override
    public Set<String> getSupportedFileExtensions() {
        return ImmutableSet.of("java");
    }

    @Override
    public Optional<String> parsePackage(String line) {
        if (!isPackage(line)) {
            return Optional.empty();
        }

        return Optional.of(extractPackageName(line));
    }

    @Override
    public List<ImportStatement> parseImport(String line, int lineNumber) {
        if (!isImport(line)) {
            if (!isPackage(line)) {
                Matcher matcher = FQCN.matcher(line);
                List<String> statements = new ArrayList<>();

                while (matcher.find()) {
                    String group = matcher.group();
                    if (group.contains(".")) {
                        statements.add(group);
                    }
                }

                return statements.stream()
                                 .map(importName -> new ImportStatement(importName, lineNumber))
                                 .collect(Collectors.toList());
            }
            return ImmutableList.of();
        }

        // There can be multiple import statements within the same line, so
        // we simply split them at their ';'
        final String[] parts = line.split(";");
        return Arrays.stream(parts)
                     .map(String::trim)
                     .filter(s -> !s.isEmpty())
                     .map(s -> s.substring(IMPORT_STATEMENT.length()))
                     .map(String::trim)
                     .map(importName -> new ImportStatement(importName, lineNumber))
                     .collect(Collectors.toList());
    }

    private boolean is(String compare, String line) {
        return line.startsWith(compare) && line.endsWith(";");
    }

    private boolean isPackage(String line) {
        return is(PACKAGE_STATEMENT, line);
    }

    private boolean isImport(String line) {
        return is(IMPORT_STATEMENT, line);
    }

    private static String extractPackageName(String line) {
        final int spaceIdx = line.indexOf(" ");
        final int semiIdx = line.indexOf(";");
        final String sub = line.substring(spaceIdx, semiIdx);
        return sub.trim();
    }

}
