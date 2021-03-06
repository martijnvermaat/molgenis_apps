package external;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.model.JDBCModelExtractor;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.MolgenisModelParser;
import org.molgenis.model.elements.Model;


public class ExtractCaTissueModel
{
	public static void main(String[] args) throws MolgenisModelException
	{
		//enable log
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
		
		MolgenisOptions options = new MolgenisOptions();
		
		options.db_driver = "com.mysql.jdbc.Driver";
		options.db_user = "molgenis";
		options.db_password = "molgenis";
		options.db_uri = "jdbc:mysql://localhost/catissue";
		
		String xml = JDBCModelExtractor.extractXml(options) ;
		
		System.out.println( xml );
		
		Model m = MolgenisModelParser.parseDbSchema(xml);
		
		String tab = ModelToExcel.write(m);
		
		System.out.println( tab );
	}
	

}
