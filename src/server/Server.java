package server;

import java.net.Socket;

public class Server {

	public Server() {
	}

	public void addClient(Socket soc) {
		if (soc == null)
			throw new IllegalArgumentException("Server: addClient(Socket): parameter null.");
		else {

		}
	}

	public boolean isTheNameAlreadyUsed(String name) {
		boolean ret = false;

		if (name == null)
			throw new IllegalArgumentException("Server: isTheNameAlreadyUsed(String): parameter null.");
		else {
			for (String s : this.playersName) {
				if (s.equals(name))
					ret = true;
			}
		}

		return ret;
	}
}