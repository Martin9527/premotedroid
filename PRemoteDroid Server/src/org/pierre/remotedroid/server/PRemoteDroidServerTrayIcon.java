package org.pierre.remotedroid.server;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.pierre.remotedroid.protocol.PRemoteDroidConnection;

public class PRemoteDroidServerTrayIcon
{
	private Preferences preferences;
	private PRemoteDroidServer server;
	private TrayIcon trayIcon;
	
	public PRemoteDroidServerTrayIcon(PRemoteDroidServer server) throws AWTException, IOException
	{
		this.server = server;
		
		this.preferences = Preferences.userNodeForPackage(PRemoteDroidServer.class);
		
		MenuItem menuItemPassword = new MenuItem("Password");
		menuItemPassword.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String password = PRemoteDroidServerTrayIcon.this.preferences.get("password", PRemoteDroidServer.DEFAULT_PASSWORD);
				password = JOptionPane.showInputDialog("Password", password);
				PRemoteDroidServerTrayIcon.this.preferences.put("password", password);
			}
		});
		
		MenuItem menuItemPort = new MenuItem("Port");
		menuItemPort.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int port = PRemoteDroidServerTrayIcon.this.preferences.getInt("port", PRemoteDroidConnection.DEFAULT_PORT);
				
				boolean ok = false;
				while (!ok)
				{
					try
					{
						String newPortString = JOptionPane.showInputDialog("Port", port);
						int newPort = Integer.parseInt(newPortString);
						PRemoteDroidServerTrayIcon.this.preferences.putInt("port", newPort);
						ok = true;
					}
					catch (NumberFormatException nfe)
					{
						nfe.printStackTrace();
					}
				}
				
				JOptionPane.showMessageDialog(null, "Restart the server to apply the new port.");
			}
		});
		
		MenuItem menuItemExit = new MenuItem("Exit");
		menuItemExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				PRemoteDroidServerTrayIcon.this.server.exit();
			}
		});
		
		PopupMenu menu = new PopupMenu();
		menu.add(menuItemPassword);
		menu.add(menuItemPort);
		menu.add(menuItemExit);
		
		this.trayIcon = new TrayIcon(ImageIO.read(new File("res/icon.png")));
		this.trayIcon.setImageAutoSize(true);
		this.trayIcon.setToolTip("PRemoteDroid server");
		this.trayIcon.setPopupMenu(menu);
		
		SystemTray.getSystemTray().add(this.trayIcon);
		
		this.trayIcon.displayMessage("PRemoteDroid", "PRemoteDroid server started", TrayIcon.MessageType.INFO);
	}
	
	public void notifyConnection(InetSocketAddress socketAddress)
	{
		this.trayIcon.displayMessage("PRemoteDroid", "New connection : " + socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort(), MessageType.INFO);
	}
	
	public void close()
	{
		SystemTray.getSystemTray().remove(this.trayIcon);
	}
}