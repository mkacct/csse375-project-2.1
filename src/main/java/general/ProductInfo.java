package general;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ProductInfo {
	private static final String PRODUCT_INFO_RES_PATH = "/product-info.properties";

	private static final ProductInfoReader READER = new ProductInfoReader();

	public static String getName() {return READER.getProperty("name");}
	public static String getVersion() {return READER.getProperty("version");}

	private static final class ProductInfoReader {
		private final Properties properties;

		private ProductInfoReader() {
			InputStream inputStream = this.getClass().getResourceAsStream(PRODUCT_INFO_RES_PATH);
			this.properties = new Properties();
			try {
				this.properties.load(inputStream);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}

		public String getProperty(String key) {
			return this.properties.getProperty(key);
		}
	}
}
