package log;

public final class Logger {
	private  final LogWindowSource defaultLogSource;
	{
		defaultLogSource = new LogWindowSource(100);
	}

	public Logger() {
	}

	public  void debug(String strMessage) {
		defaultLogSource.append(LogLevel.Debug, strMessage);
	}

	public  void error(String strMessage) {
		defaultLogSource.append(LogLevel.Error, strMessage);
	}

	public  LogWindowSource getDefaultLogSource() {
		return defaultLogSource;
	}
}
