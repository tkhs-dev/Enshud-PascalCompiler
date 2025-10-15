package enshud.s1.lexer;

import enshud.pascal.TokenDef;
import enshud.token.Token;
import enshud.token.TokenFile;

import java.io.IOException;
import java.util.List;

public class Lexer {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// normalの確認
		System.out.println(new Lexer().run("data/pas/normal01.pas", "tmp/out1.ts"));
		System.out.println(new Lexer().run("data/pas/normal02.pas", "tmp/out2.ts"));
		System.out.println(new Lexer().run("data/pas/normal03.pas", "tmp/out3.ts"));
	}

	/**
	 * TODO
	 * 
	 * 開発対象となるLexer実行メソッド．
	 * 以下の仕様を満たすこと．
	 * 
	 * 仕様:
	 * 第一引数で指定されたpasファイルを読み込み，トークン列に分割する．
	 * トークン列は第二引数で指定されたtsファイルに書き出すこと．
	 * 正常に処理が終了した場合は"OK"を，入力ファイルが見つからない場合は"File not found"を返す．
	 * 
	 * @param inputFileName 入力pasファイル名
	 * @param outputFileName 出力tsファイル名
	 */

	public String run(final String inputFileName, final String outputFileName) {
		Tokenizer tokenizer;
		String input = "";
		List<Token> tokens = new java.util.ArrayList<>();
		// ファイルが存在するか確認
		if (!java.nio.file.Files.exists(java.nio.file.Paths.get(inputFileName))) {
			return "File not found";
		}

		try {
			input = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(inputFileName)));
			tokenizer = new Tokenizer(input);
			while (tokenizer.hasNext()) {
				Token token = tokenizer.nextToken();
				if (token != null) {
					tokens.add(token);
				} else {
					break;
				}
			}

			TokenFile.SaveTokens(outputFileName, tokens);
		} catch (TokenizerException e) {
			return "Invalid identifier: line " + e.lineNumber;
		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}
		return "OK";
    }
}

class Tokenizer {
	private String input;
	private int position;
    private int lineNumber;

    public Tokenizer(String input) {
		this.input = input;
		this.position = 0;
        this.lineNumber = 1;
	}

	public boolean hasNext() {
        // 空白や改行しかない場合もfalseを返す
        int tmpPos = position;
        while (tmpPos < input.length() && Character.isWhitespace(input.charAt(tmpPos))) {
            tmpPos++;
        }
        return tmpPos < input.length();
	}

	public Token nextToken() throws TokenizerException {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            // 空白や改行をスキップ
            position++;
            if (input.charAt(position - 1) == '\n') {
                lineNumber++;
            }
        }

		String remainingInput = input.substring(position).trim();

		for(TokenDef token : TokenDef.SortedTokens) {
			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^" + token.regex);
			java.util.regex.Matcher matcher = pattern.matcher(remainingInput);
			if (matcher.find()) {
				String matched = matcher.group();
				int startIndex = remainingInput.indexOf(matched);
				if (startIndex != 0) {
					// マッチした部分が先頭にない場合はスキップ
					continue;
				}
				position += startIndex + matched.length();
				if (token == TokenDef.COMMENT) {
					return nextToken();
				}
				return new Token(matched, token.tokenName, token.id, lineNumber);
			}
		}
		// 識別子はアルファベットのみで構成されるため，残りがあってかつここに到達した場合は不正な識別子
		if (!remainingInput.isEmpty()) {
			int lineNumber = input.substring(0, position).split("\n").length + 1;
			throw new TokenizerException("Invalid identifier at line " + lineNumber, lineNumber);
		}
		return null;
	}
}

class TokenizerException extends Exception {
	int lineNumber;

	public TokenizerException(String message, int lineNumber) {
		super(message);
		this.lineNumber = lineNumber;
	}
}
