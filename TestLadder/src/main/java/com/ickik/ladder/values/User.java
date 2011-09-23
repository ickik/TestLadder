package com.ickik.ladder.values;

public class User {

	private long id;
	private String login;
	private String password;
	private String rights;
	
	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public String getRights() {
		return rights;
	}
	
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof User) {
			User u = (User) arg0;
			if (u.getId() == 0 || id == 0) {
				if (u.getLogin().equals(login)) {
					return true;
				}
			}else if (u.getId() == id) {
				return true;
			}
		}
		return super.equals(arg0);
	}
	
	@Override
	public int hashCode() {
		return new Long(id).hashCode();
	}
	
//	/**
//	 * Convert "fsd" into "0110100".
//	 * @param rights
//	 * @return
//	 */
//	public static String getConvertStringToBinaryString(String rights) {
//		byte[] bytes = rights.getBytes();
//		StringBuilder binary = new StringBuilder();
//		for (byte b : bytes) {
//			int val = b;
//			for (int i = 0; i < 8; i++) {
//				binary.append((val & 128) == 0 ? 0 : 1);
//				val <<= 1;
//			}
//		}
//		return binary.toString();
//	}
//	
//	/**
//	 * Convert binary String ("00111011") into "zmpojf".
//	 * @param binaryRights
//	 * @return
//	 */
//	public static String getConvertBinaryStringToString(String binaryRights) {
//		int len = binaryRights.length();
//		int arrayCase = len / 8;
//		
//		if (arrayCase % 8 != 0) {
//			arrayCase++;
//		}
//		byte[] binary = new byte[arrayCase];
//		for (int i = 0; i < arrayCase; i++) {
//			int b = 0;
//			int j;
//			for (j = 0; j < 8 && ((i * 8) + j) < len; j++) {
//				char c = binaryRights.charAt((i * 8) + j);
//				b <<= 1;
//				if (c == '0') {
//					b |= 0;
//				} else {
//					b |= 1;
//				}
//			}
//			if (j != 8) {
//				for (; j < 8; j++) {
//					b <<= 1;
//					b |= 0;
//				}
//			}
//			binary[i] = (byte) b;
//		}
//		return new String(binary);
//	}
}
