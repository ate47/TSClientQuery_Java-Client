package fr.harmonia.tsclientquery.answer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultipleBanAnswer extends Answer implements Iterable<BanAnswer> {
	private List<BanAnswer> list;

	public MultipleBanAnswer() {
		super("");
		list = new ArrayList<>();
	}

	public void addBan(String line) {
		list.add(new BanAnswer(line));
	}

	@Override
	public Iterator<BanAnswer> iterator() {
		return list.iterator();
	}
	
	public List<BanAnswer> getBanList() {
		return list;
	}

}
