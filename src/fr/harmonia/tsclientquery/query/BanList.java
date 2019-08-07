package fr.harmonia.tsclientquery.query;

/*
Usage: banlist

Displays a list of active bans on the selected virtual server.

Example:
   banlist
   banid=7 ip=1.2.3.4 created=1259444002242 invokername=Sven invokercldbid=56
   invokeruid=oHhi9WzXLNEFQOwAu4JYKGU+C+c= reason enforcements=0
   error id=0 msg=ok

error id=0 msg=ok
 */
public class BanList extends NoAnswerQuery {

	public BanList() {
		super("banlist");
	}

}
