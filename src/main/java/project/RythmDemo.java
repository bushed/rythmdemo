package project;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.rythmengine.Rythm.Mode;
import org.rythmengine.RythmEngine;
import org.rythmengine.conf.RythmConfigurationKey;
import static java.io.File.separator;


final public class RythmDemo {
	private static final String TEMPLATE_DIR = FileUtils.getTempDirectoryPath();
	private static final String MAIN_NAME = "main.rythm";
	private static final String INVOCABLE_NAME = "invocable.rythm";
	private static final String INVOCABLE_TEMPLATE =
			"@args String param        \n" +
			"                          \n" +
			"[:: invocable body]       \n" +
			"param = @param            \n" +
			"[:: end of invocable body]\n";
	private static final String MAIN_TEMPLATE =
			"[main body]               \n" +
			"                          \n" +
			"@invocable({              \n" +
			"    param : \"value\"     \n" +
			"})                        \n";


	public static void main(String... args) throws IOException, InterruptedException {
	// create files in temp dir
		final File mainFile = new File(TEMPLATE_DIR + separator + MAIN_NAME);
		final File invocableFile = new File(TEMPLATE_DIR + separator + INVOCABLE_NAME);
		FileUtils.writeStringToFile(mainFile, MAIN_TEMPLATE);
		FileUtils.writeStringToFile(invocableFile, INVOCABLE_TEMPLATE);

	// init engine
		final RythmEngine engine = getRythmEngine();

	// Try render main page
		tryRender(engine, "first render: ");

	// now wait and change the template
		TimeUnit.MILLISECONDS.sleep(5100);
		FileUtils.writeStringToFile(mainFile, MAIN_TEMPLATE + " some tail");

	// Fails now !
		try {
			tryRender(engine, "after changes: ");
		} finally {
			FileUtils.deleteQuietly(mainFile);
			FileUtils.deleteQuietly(invocableFile);
			engine.shutdown();
		}
	}

	private static RythmEngine getRythmEngine() {
		Map<String, Object> conf = new HashMap<>();
		conf.put(RythmConfigurationKey.ENGINE_MODE.getKey(), Mode.dev);
		conf.put(RythmConfigurationKey.HOME_TEMPLATE.getKey(), TEMPLATE_DIR);
		return new RythmEngine(conf);
	}

	private static void tryRender(final RythmEngine engine, final String comment) {
		final String firstResult = engine.render(MAIN_NAME);
		System.out.println();
		System.out.println(comment);
		System.out.println("-------------");
		System.out.println(firstResult);
	}
}
