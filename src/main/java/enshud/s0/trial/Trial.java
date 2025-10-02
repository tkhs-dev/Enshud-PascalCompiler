package enshud.s0.trial;

import java.io.File;

public class Trial {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// normalの確認
		System.out.println(new Trial().run("data/pas/normal01.pas"));
		System.out.println(new Trial().run("data/pas/normal02.pas"));
		System.out.println(new Trial().run("data/pas/normal03.pas"));
		System.out.println(new Trial().run("data/pas/normalXX.pas"));
	}

	/**
	 * TODO
	 * 
	 * 開発対象となるTrial実行メソッド （練習用）．
	 * 以下の仕様を満たすこと．
	 * 
	 * 仕様:
	 * 第一引数で指定されたpascalファイルを読み込み，ファイル行数を文字列として返す．
	 * 入力ファイルが見つからない場合は"File not found"を返す．
	 * 
	 * @param inputFileName 入力pascalファイル名
	 */
	public String run(final String inputFileName) {

		File file = new File(inputFileName);
		if (file.exists()) {
			// ファイルが存在する場合、行数をカウントして返す
			try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
				int lineCount = 0;
				while (reader.readLine() != null) {
					lineCount++;
				}
				return Integer.toString(lineCount);
			} catch (java.io.IOException e) {
				e.printStackTrace();
				return "Error reading file";
			}
		} else {
			// ファイルが存在しない場合
			return "File not found";
		}
	}
}
