/* Date:        October 28, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.welcome;

import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.html.JQueryTreeView;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTree;
import org.molgenis.util.Tuple;

import plugins.emptydb.emptyDatabase;
import plugins.fillanimaldb.FillAnimalDB;

import commonservice.CommonService;

public class AnimalDBWelcomeScreenPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5861419875983400033L;
	
	private JQueryTreeView treeViewer;

	public AnimalDBWelcomeScreenPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
		
		SimpleTree myTree = new SimpleTree("myTree", null);
		SimpleTree mySubTree1 = new SimpleTree("mySubTree1", myTree);
		SimpleTree mySubTree2 = new SimpleTree("mySubTree2", myTree);
		SimpleTree mySubSubTree = new SimpleTree("mySubSubTree", mySubTree1);
		
		treeViewer = new JQueryTreeView("myTreeViewer", myTree);
	}
	
	public String renderTreeViewer() {
		return treeViewer.render();
	}

	@Override
	public String getViewName()
	{
		return "plugins_welcome_AnimalDBWelcomeScreenPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/welcome/AnimalDBWelcomeScreenPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//replace example below with yours
//		try
//		{
//		Database db = this.getDatabase();
//		String action = request.getString("__action");
//		
//		if( action.equals("do_add") )
//		{
//			Experiment e = new Experiment();
//			e.set(request);
//			db.add(e);
//		}
//		} catch(Exception e)
//		{
//			//e.g. show a message in your form
//		}
	}

	@Override
	public void reload(Database db)
	{
		// Entry point when logging in, so good place to (re)set the ObservationTarget label map
		CommonService cs = CommonService.getInstance();
		cs.setDatabase(db);
		cs.makeObservationTargetNameMap(this.getLogin().getUserId(), true);
	}
	
}
