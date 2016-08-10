package org.midnightas.mau;

public class Token {
	
	public TokenType type;
	public Object value;
	public int row, col;
	
	public Token(TokenType type, Object value, int row, int col) {
		this.type = type;
		if(this.type == TokenType.NUMBER)
			this.value = Double.parseDouble(value.toString());
		else
			this.value = value;
		this.row = row;
		this.col = col;
	}
	
	public String toString() {
		return String.format("(%s, %s, %s:%s)", type, value, row, col);
	}
	
	public boolean isIdentifier(String value) {
		return type == TokenType.IDENTIFIER && value.equals(this.value);
	}
	
	public boolean isIdentifierIgnoreCase(String value) {
		return type == TokenType.IDENTIFIER && value.equalsIgnoreCase(this.value.toString());
	}

	public String makeSureItsAnIdentifier(int row, int col) throws MauException {
		if(type != TokenType.IDENTIFIER)
			throw new MauException("Must be an identifier", row, col);
		return value.toString();
	}

	public String makeSureItsAnIdentifier() throws MauException {
		return makeSureItsAnIdentifier(row, col);
	}
	
}
