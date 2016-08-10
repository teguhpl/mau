package org.midnightas.mau;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

import org.midnightas.mau.builtin.MauNumber;
import org.midnightas.mau.builtin.MauString;

public class Mau implements VariableContainer {

	public static HashMap<String, MauModule> modules = new HashMap<String, MauModule>();

	public static final void main(String[] args) {
		Mau mau = new Mau();
		try {
			mau.exec(new String(Files.readAllBytes(new File(args[0]).toPath()), "UTF-8"));
			System.out.println(mau.vars);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MauException e) {
			System.err.println("Mau error: " + e.getMessage());
			System.err.println("Row " + e.row + ", Col " + e.col);
		}
	}

	boolean setModule = false;

	public HashMap<String, MauInstance> vars = new HashMap<String, MauInstance>();
	public List<String> privateVars = new ArrayList<String>();

	public MauModule myModule;

	private int executionRow, executionCol;

	public HashMap<String, MauInstance> fargs;
	
	public Mau() {

	}

	public void exec(String text) throws MauException {
		exec(tokenize(text));
	}
	
	public void function(String text, HashMap<String, MauInstance> code) throws MauException {
		fargs.putAll(code);
		exec(text);
		fargs.clear();
	}

	public void exec(List<Token> tokens) throws MauException {
		for (int tl = 0; tl < tokens.size(); tl++) {
			Token t = tokens.get(tl);
			executionRow = t.row;
			executionCol = t.col;
			if (t.isIdentifier("module")) {
				if (setModule)
					throw new MauException("One module per file.", t.row, t.col);
				setModule = true;
				myModule = new MauModule();
				modules.put(tokens.get(tl + 1).makeSureItsAnIdentifier(t.row, t.col), myModule);
			} else if (t.type == TokenType.OASSIGN) {
				String name = tokens.get(tl - 1).makeSureItsAnIdentifier(t.row, t.col);
				List<Token> value = new ArrayList<Token>();
				int tl0;
				for (tl0 = tl + 1; tl0 < tokens.size(); tl0++) {
					Token t0 = tokens.get(tl0);
					if (t0.type == TokenType.SEMICOLON) {
						break;
					} else
						value.add(t0);
				}
				if (!vars.containsKey(name)) {
					String type = tokens.get(tl - 2).makeSureItsAnIdentifier(t.row, t.col);
					vars.put(name, evaluate(modules.get(type), value));
				}
				tl = tl0;
			} else if(t.type == TokenType.LSMBRACKET) {
				{
					int deep = 0;
					for (int tl0 = tl; tl0 >= 0; tl0--) {
						Token t0 = tokens.get(tl0);
						if (t0.type == TokenType.LCBRACKET && deep == 0) {
							tl = tl0;
							break;
						} else if (t0.type == TokenType.LCBRACKET)
							deep--;
						else if (t0.type == TokenType.RCBRACKET)
							deep++;
					}
				}
				Token tok = tokens.get(++tl);
				VariableContainer object = getContent(tok);
				while (tokens.get(tl + 1).type == TokenType.DOT && tokens.get(tl + 2).type == TokenType.IDENTIFIER) {
					String key = tokens.get(tl + 2).value.toString();
					System.out.println(object);
					if (object.getVar(key, tok.row, tok.col) != null) {
						object = (VariableContainer) object.getVar(key, tok.row, tok.col);
					} else
						break;
					tok = tokens.get(tl + 1);
					tl += 2;
					if (object == null)
						throw new MauException("Unknown variable: " + tok.value.toString(), tok.row, tok.col);
				}
				List<Token> tempArg = new ArrayList<Token>();
				HashMap<String, MauInstance> args = new HashMap<String, MauInstance>();
				outerloop: for (int tl0 = tl + 2; tl0 < tokens.size(); tl0++) {
					Token t0 = tokens.get(tl0);
					if (t0.type == TokenType.OASSIGN) {
						String varName = tokens.get(tl0 - 1).value.toString();
						int deep = 0;
						for (int tl00 = tl0; tl00 < tokens.size(); tl00++) {
							Token t00 = tokens.get(tl00);
							if ((t00.type == TokenType.COMMA || t00.type == TokenType.RSMBRACKET) && deep == 0) {
								tl0 = tl00;
								args.put(varName, evaluate(((MauFunction) object).args.get(varName), tempArg));
								tempArg = new ArrayList<Token>();
								if (t00.type == TokenType.RSMBRACKET) {
									tl = tl0;
									break outerloop;
								}
							} else {
								tempArg.add(t00);
								if (t00.type == TokenType.LSMBRACKET)
									deep++;
								else if (t00.type == TokenType.RSMBRACKET)
									deep--;
							}
						}
					} else if(t0.type == TokenType.RSMBRACKET) {
						tl = tl0;
						System.out.println(tokens.get(tl));
						break;
					}
				}
				((MauFunction) object).call(args);
			}
		}
	}

	public MauInstance evaluate(MauModule type, List<Token> tokens) throws MauException {
		System.out.println(tokens);
		for (int tl = 0; tl < tokens.size(); tl++) {
			Token t = tokens.get(tl);
			executionRow = t.row;
			executionCol = t.col;
			if (t.type == TokenType.OADD) {
				Token t0 = tokens.remove(tl - 1);
				tokens.remove(tl - 1);
				Token t1 = tokens.remove(--tl);
				MauInstance c0 = getContent(t0);
				MauInstance c1 = getContent(t1);
				if (c0.module instanceof MauString || c1.module instanceof MauString)
					tokens.add(tl, new Token(TokenType.STRING, t0.value + "" + t1.value, t0.row, t0.col));
				else
					tokens.add(tl, new Token(TokenType.NUMBER,
							((Double) c0.vars.get("value")) + ((Double) c1.vars.get("value")), t0.row, t0.col));
			} else if (t.type == TokenType.OSUB) {
				Token t0 = tokens.remove(tl - 1);
				tokens.remove(tl - 1);
				Token t1 = tokens.remove(--tl);
				MauInstance c0 = getContent(t0);
				MauInstance c1 = getContent(t1);
				if (c0.module instanceof MauString || c1.module instanceof MauString)
					tokens.add(tl, new Token(TokenType.STRING, t0.value.toString().replaceAll(t1.value.toString(), ""),
							t0.row, t0.col));
				else
					tokens.add(tl, new Token(TokenType.NUMBER,
							((Double) c0.vars.get("value")) + ((Double) c1.vars.get("value")), t0.row, t0.col));
			}
		}
		Token remainingToken = tokens.remove(0);
		MauInstance result = getContent(remainingToken);
		if (result.module.equals(type))
			return result;
		else
			throw new MauException("Result not of type " + type, remainingToken.row, remainingToken.col);
	}

	public MauInstance getContent(Token t) throws MauException {
		MauInstance result;
		if (t.type == TokenType.NUMBER)
			result = modules.get("number").newInstance(t.value);
		else if (t.type == TokenType.STRING)
			result = modules.get("string").newInstance(t.value);
		else
			result = vars.get(t.makeSureItsAnIdentifier());
		return result;
	}

	@Override
	public void setVar(String name, MauInstance value, boolean isPrivate) {
		vars.remove(name);
		vars.put(name, value);
		if (isPrivate)
			privateVars.add(name);
	}

	@Override
	public MauInstance getVar(String name, int row, int col) throws MauException {
		if (privateVars.contains(name))
			throw new MauException("Non-visible variable " + name, executionRow, executionCol);
		return vars.get(name);
	}

	public static List<Token> tokenize(String content) throws MauException {
		List<Token> tokens = new ArrayList<Token>();
		Matcher m = TokenType.PATTERN.matcher(content);
		int row, col;
		row = col = 1;
		while (m.find()) {
			String group = m.group();
			TokenType type = TokenType.fromInput(group);
			if (type == TokenType.UNKNOWN)
				throw new MauException("Unknown token " + group, row, col);
			else if (type == TokenType.SPACE || type == TokenType.COMMENT)
				col += group.length();
			else if (type == TokenType.NEWLINE) {
				col = 1;
				row++;
			} else {
				tokens.add(new Token(type, type == TokenType.STRING ? group.substring(1, group.length() - 1) : group,
						row, col));
				col += group.length();
			}
		}
		tokens.add(new Token(TokenType.EOF, 0, row, col));
		System.out.println(tokens);
		return tokens;
	}

	public static <T> String joinString(String delimiter, List<T> objs) {
		String output = "";
		for (int i = 0; i < objs.size(); i++) {
			output = output + delimiter + objs.get(i);
		}
		output = output.substring(1);
		return output;
	}

	static {
		modules.put("number", new MauNumber());
		modules.put("string", new MauString());
		modules.put("std", new MauModule() {
			{
				classVars.put("out", new MauFunction() {
					@Override
					public void call(HashMap<String, MauInstance> args) throws MauException {
						System.out.println(args.get("obj"));
					}
				});
			}
		});
	}

}
