package fr.harmonia.tsclientquery.event;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import fr.harmonia.tsclientquery.TSClientQuery;
import fr.harmonia.tsclientquery.exception.QueryException;
import fr.harmonia.tsclientquery.objects.ChannelClient;
import fr.harmonia.tsclientquery.objects.Client;

public class BasicClientListHandler implements Handler {
	private class ClientListCollection implements Collection<Client> {
		private Collection<Client> viewPointedClients;

		@Override
		public boolean add(Client e) {
			return viewPointedClients.add(e);
		}

		@Override
		public boolean addAll(Collection<? extends Client> c) {
			return viewPointedClients.addAll(c);
		}

		@Override
		public void clear() {
			viewPointedClients.clear();
		}

		@Override
		public boolean contains(Object o) {
			return viewPointedClients.contains(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return viewPointedClients.containsAll(c);
		}

		@Override
		public boolean isEmpty() {
			return viewPointedClients.isEmpty();
		}

		@Override
		public Iterator<Client> iterator() {
			return viewPointedClients.iterator();
		}

		@Override
		public boolean remove(Object o) {
			return viewPointedClients.remove(o);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return viewPointedClients.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return viewPointedClients.retainAll(c);
		}

		@Override
		public int size() {
			return viewPointedClients.size();
		}

		@Override
		public Object[] toArray() {
			return viewPointedClients.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return viewPointedClients.toArray(a);
		}

	}

	private TSClientQuery client;
	private final ClientListCollection viewClients = new ClientListCollection();
	// best name ever
	private final ConcurrentMap<Integer, ConcurrentMap<Integer, Client>> SCHANDLERID_TO_CLID_TO_CLIENT = new ConcurrentHashMap<>();

	public BasicClientListHandler(TSClientQuery client) {
		this.client = client;
	}

	private void addClient(int schandlerid, ChannelClient client) {
		mergeClientOrCreate(schandlerid, client);
	}

	private void deleteClient(int schandlerid, int clientID) {
		moveClient(schandlerid, 0, clientID);
	}

	private void doForClient(int schandlerid, int clid, Consumer<MutableClient> modifier) {
		ConcurrentMap<Integer, Client> clidMap = SCHANDLERID_TO_CLID_TO_CLIENT.get(schandlerid);
		if (clidMap == null)
			return;
		MutableClient client = (MutableClient) clidMap.get(clid);
		if (client != null)
			modifier.accept(client);
	}

	public Client getClient(int schandlerid, int clid) {
		ConcurrentMap<Integer, Client> clidMap = SCHANDLERID_TO_CLID_TO_CLIENT.get(schandlerid);

		if (clidMap == null)
			return null;

		return clidMap.get(clid);
	}

	public Collection<Client> getClients(int schandlerid) {
		return SCHANDLERID_TO_CLID_TO_CLIENT
				.computeIfAbsent(schandlerid, id -> new ConcurrentHashMap<Integer, Client>()).values();
	}

	public Collection<Client> getViewClients() {
		return viewClients;
	}

	public void init() {
		client.clientNotifyRegister(EnumEvent.notifyclientleftview);
		client.clientNotifyRegister(EnumEvent.notifycliententerview);
		client.clientNotifyRegister(EnumEvent.notifyclientmoved);
		client.clientNotifyRegister(EnumEvent.notifycurrentserverconnectionchanged);
		client.clientNotifyRegister(EnumEvent.notifyconnectstatuschange);

		int schandlerid = client.currentServerConnectionHandlerID();
		queryClients(schandlerid);

		viewClients.viewPointedClients = getClients(schandlerid);
	}

	private void mergeClientOrCreate(int schandlerid, ChannelClient client) {
		ConcurrentMap<Integer, Client> clidMap = SCHANDLERID_TO_CLID_TO_CLIENT.computeIfAbsent(schandlerid,
				id -> new ConcurrentHashMap<Integer, Client>());
		int clid = client.getClientID();
		MutableClient old = (MutableClient) clidMap.get(clid);
		if (old == null) {
			clidMap.put(clid, new MutableClient(client));
		} else {
			old.update(client, false);
		}
	}

	private void mergeClientOrCreate(int schandlerid, List<ChannelClient> clients) {
		ConcurrentMap<Integer, Client> clidMap = SCHANDLERID_TO_CLID_TO_CLIENT.computeIfAbsent(schandlerid,
				id -> new ConcurrentHashMap<Integer, Client>());
		for (ChannelClient client : clients) {
			int clid = client.getClientID();
			MutableClient old = (MutableClient) clidMap.get(clid);
			if (old == null) {
				clidMap.put(clid, new MutableClient(client));
			} else {
				old.update(client, false);
			}
		}
	}

	private void moveClient(int schandlerid, int channelToID, int clientID) {
		doForClient(schandlerid, clientID, c -> c.changeChannel(channelToID));
	}

	@Override
	public void onChangeCurrentServerConnection(int schandlerid) {
		if (!SCHANDLERID_TO_CLID_TO_CLIENT.containsKey(schandlerid))
			queryClients(schandlerid);
		viewClients.viewPointedClients = getClients(schandlerid);
	}

	@Override
	public void onClientConnect(int schandlerid, int channelToID, Client client) {
		addClient(schandlerid, client);
	}

	@Override
	public void onClientDisconnect(int schandlerid, int channelFromID, int clientID) {
		deleteClient(schandlerid, clientID);
	}

	@Override
	public void onClientEnterView(int schandlerid, int channelFromID, int channelToID, Client client) {
		addClient(schandlerid, client);
	}

	@Override
	public void onClientEnterViewMoved(int schandlerid, int channelFromID, int channelToID, int invokerid,
			String invokername, String invokeruid, Client client) {
		addClient(schandlerid, client);
	}

	@Override
	public void onClientKickFromChannel(int schandlerid, int channelTargetID, int invokerClientID, String invokerName,
			String invokerUID, String reasonmsg, int clientID) {
		moveClient(schandlerid, channelTargetID, clientID);
	}

	@Override
	public void onClientKickFromChannelOutOfView(int schandlerid, int channelFromID, int channelToID, int invokerid,
			String invokername, String invokeruid, String reasonmsg, Client client) {
		addClient(schandlerid, client);
	}

	@Override
	public void onClientKickFromServer(int schandlerid, int channelFromID, int invokerClientID, String invokerName,
			String invokerUID, String reasonmsg, int clientID) {
		deleteClient(schandlerid, clientID);
	}

	@Override
	public void onClientLeftView(int schandlerid, int channelTargetID, int clientID) {
		deleteClient(schandlerid, clientID);
	}

	@Override
	public void onClientMove(int schandlerid, int channelTargetID, int clientID) {
		moveClient(schandlerid, channelTargetID, clientID);
	}

	@Override
	public void onClientMovedByOther(int schandlerid, int channelTargetID, int invokerClientID, String invokerName,
			String invokerUID, int clientID) {
		moveClient(schandlerid, channelTargetID, clientID);
	}

	@Override
	public void onConnectionEstablished(int schandlerid) {
		queryClients(schandlerid);
	}

	@Override
	public void onDisconnected(int schandlerid, int error) {
		ConcurrentMap<Integer, Client> old = SCHANDLERID_TO_CLID_TO_CLIENT.remove(schandlerid);
		old.clear();
	}

	private void queryClients(int schandlerid) {
		try {
			client.use(schandlerid, client -> mergeClientOrCreate(schandlerid, client.clientList()));
		} catch (QueryException e) {
		}
	}

}
