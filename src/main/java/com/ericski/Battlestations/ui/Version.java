package com.ericski.Battlestations.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum Version
{
	INSTANCE;

	private static final Logger logger = LogManager.getLogger(Version.class);

	private static final String BUILD_NUMBER = "build.number";
	private static final String VERSION_MINOR = "version.minor";
	private static final String VERSION_MAJOR = "version.major";
	private static final String VERSIONURL = "http://ericski.com/bs/build.number";
	private final Properties buildProps = new Properties();
	private Properties serverProps = null;

	public static enum VersionUpToDate
	{
		Yes,
		No,
		Hostdown,
		Unknown;
	}

	private Version()
	{
		InputStream stream = getClass().getResourceAsStream("/build.number");
		try
		{
			buildProps.load(stream);
			stream.close();
		}
		catch (IOException e)
		{
			// not a lot that can be done
		}
	}

	public String getMajor()
	{
		return buildProps.getProperty(VERSION_MAJOR);
	}

	public String getMinor()
	{
		return buildProps.getProperty(VERSION_MINOR);
	}

	public String getBuildNumber()
	{
		return buildProps.getProperty(BUILD_NUMBER);
	}

	public String getBuildDate()
	{
		return buildProps.getProperty("build.date");
	}

	public String getVersion()
	{
		return getMajor() + "." + getMinor() + " build " + getBuildNumber();
	}

	public String getServerVersion()
	{
		if (serverProps == null)
		{
			isUptoDate();
		}
		String serverMajor = serverProps.getProperty(VERSION_MAJOR, "UNKNOWN");
		String serverMinor = serverProps.getProperty(VERSION_MINOR, "UNKNOWN");
		String serverBuild = serverProps.getProperty(BUILD_NUMBER, "UNKNOWN");

		String serverString = serverMajor + "." + serverMinor + " build " + serverBuild;
		return serverString;
	}

	public VersionUpToDate isUptoDate()
	{
		serverProps = new Properties();
		try
		{
			URL url = new URL(VERSIONURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(false);
			serverProps.load(conn.getInputStream());
			conn.disconnect();

			String serverMajor = serverProps.getProperty(VERSION_MAJOR);
			String serverMinor = serverProps.getProperty(VERSION_MINOR);
			String serverBuild = serverProps.getProperty(BUILD_NUMBER);

			if (Integer.parseInt(serverMajor) <= Integer.parseInt(getMajor()))
			{
				if (Integer.parseInt(serverMinor) <= Integer.parseInt(getMinor()))
				{
					if (Integer.parseInt(serverBuild) <= Integer.parseInt(getBuildNumber()))
					{
						return VersionUpToDate.Yes;
					}
					else
					{
						System.out.println("Newer build exists");
					}

				}
				else
				{
					System.out.println("Newer minor release exists");
				}
			}
			else
			{
				System.out.println("Newer major release exists");
			}

			return VersionUpToDate.No;
		}
		catch (IOException | NumberFormatException e)
		{
			logger.warn("Exception getting remote version", e);
			return VersionUpToDate.Hostdown;
		}
	}

	public static void main(String[] args)
	{
		System.out.println("    This version: " + Version.INSTANCE.getVersion());
		System.out.println("Uploaded version: " + Version.INSTANCE.getServerVersion());
		System.out.println("Up to date? " + Version.INSTANCE.isUptoDate().name());
	}
}
