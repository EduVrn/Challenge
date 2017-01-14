package challenge.dbside.models.ini;

public enum TypeEntity {
	USER(1),
	CHALLENGE_DEFINITION(2),
	CHALLENGE_INSTANCE(3),
	COMMENT(4),
        IMAGE(5);
	
	private final int value;
	
	private TypeEntity(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
}
