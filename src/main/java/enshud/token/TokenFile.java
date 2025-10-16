package enshud.token;

import java.io.IOException;
import java.util.List;

public class TokenFile {
    public final String fileName;

    public TokenFile(String fileName) {
        this.fileName = fileName;
    }

    public List<Token> getTokens() throws FileFormatException, IOException {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(fileName))) {
            List<Token> tokens = new java.util.ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 4) {
                    String tokenStr = parts[0];
                    String tokenName = parts[1];
                    int id = Integer.parseInt(parts[2]);
                    int lineNum = Integer.parseInt(parts[3]);
                    tokens.add(new Token(tokenStr, tokenName, id, lineNum));
                }
            }
            return tokens;
        } catch (NumberFormatException e) {
            throw new FileFormatException("Invalid number format in token file: " + e.getMessage());
        }
    }

    public static void saveTokens(String fileName, List<Token> tokens) throws IOException{
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(fileName))) {
            for (Token token : tokens) {
                writer.write(String.format("%s\t%s\t%d\t%d\n", token.token, token.tokenName, token.id, token.line));
            }
        }
    }
}
