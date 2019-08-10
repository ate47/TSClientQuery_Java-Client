package fr.harmonia.tsclientquery.ban;

public class Ban {
	private String banreason;
	private int id;
	private String ip;
	private String regex;
	private long timeInSeconds = -1L;
	private String uid;
	
	public Ban(String banreason, int id, String ip, String regex, long timeInSeconds, String uid) {
		this.banreason = banreason;
		this.id = id;
		this.ip = ip;
		this.regex = regex;
		this.timeInSeconds = timeInSeconds;
		this.uid = uid;
	}
	public String getBanReason() {
		return banreason;
	}
	
	public int getId() {
		return id;
	}
	public String getIp() {
		return ip;
	}
	public String getNameRegex() {
		return regex;
	}
	
	public long getTimeInSeconds() {
		return timeInSeconds;
	}
	public String getUid() {
		return uid;
	}
	public boolean isInfinite() {
		return timeInSeconds <= 0;
	}
}
