/* Date:        November 11, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.header;

import org.apache.commons.lang.StringUtils;
import org.molgenis.auth.DatabaseLogin;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.EmailService;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import plugins.emptydb.emptyDatabase;
import plugins.fillanimaldb.FillAnimalDB;
import app.FillMetadata;


/**
 * A simple plugin to create the header of the MOLGENIS application. This
 * includes the header logo as well as the top level menu items for
 * documentation, services etc (replaces the hardcoded header).
 * 
 * @author Morris Swertz
 */
public class AnimalDBHeader extends PluginModel<Entity>
{
	private String feedback = null;
	private static final long serialVersionUID = 4701628601897969977L;
	
	public AnimalDBHeader(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders() {
		return  "<script src=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.js\" language=\"javascript\"></script>\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.css\">" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.rounded.css\">" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.roundedBr.css\">";		
	}

	@Override
	public String getViewName()
	{
		return "plugins_header_AnimalDBHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/header/AnimalDBHeader.ftl";
	}

	@Override
	public void reload(Database db)
	{
		try {
			int nrOfUsersInDb = db.count(MolgenisUser.class);
			if (nrOfUsersInDb == 0) { // Check if DB is filled by counting the nr. of users (should always be >= 2)
				prefillDb(db);
			}
		} catch (Exception e) {
			prefillDb(db);
		}
	}
	
	private void prefillDb(Database db) {
		try {
			// Empty DB and run generated sql scripts
			new emptyDatabase(db, false);
			FillMetadata.fillMetadata(db, false);
			
			// Populate db with targets, features, values etc. needed to make AnimalDB run
			FillAnimalDB myFillAnimalDB = new FillAnimalDB(db);
			myFillAnimalDB.populateDB(this.getLogin());
			
			this.getMessages().add(new ScreenMessage("Your database was empty, so it was prefilled with entities needed to make AnimalDB run", true));
		} catch (Exception e) {
			String message = "Something went wrong while trying to prefill your database";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
		if ("doLogout".equals(request.getAction())) {
			getLogin().logout(db);
		}
		
		if ("sendFeedback".equals(request.getAction())) {
			feedback = "User: " + request.getString("name") + " (username: " + this.getLogin().getUserName() + 
					") sent: " + request.getString("feedback") + " about: " + request.getString("plugin");
			
			// get admin email
			MolgenisUser admin = db.query(MolgenisUser.class).eq(MolgenisUser.NAME, "admin").find().get(0);
			if (StringUtils.isEmpty(admin.getEmail()))
				throw new DatabaseException("Sending feedback failed: the administrator has no email address set. Please contact your administrator about this.");
			
			EmailService ses = this.getEmailService();
			ses.email("New feedback on AnimalDB", feedback, admin.getEmail(), true);
			
			this.getMessages().add(new ScreenMessage(feedback, true));
		}
		
		if ("resetFeedbackForm".equals(request.getAction())) {
			feedback = null;
		}
		} catch (Exception e)
		{
			e.printStackTrace();
			this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			//this.setError(e.getMessage());
		}
	}
	
	public String getActivePlugin() {
		if (this.getParent().getSelected() == null) {
			return "";
		}
		ScreenModel model = this.getParent().getSelected();
		while (model.getSelected() != null) {
			model = model.getSelected();
		}
		return model.getLabel();
	}

	public String getFullUserName() {
		
		if (this.getLogin().isAuthenticated()) {
			return ((DatabaseLogin)this.getLogin()).getFullUserName();
		}
		return null;
	}
	
	public String getFeedback() {
		return feedback;
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}
	
}
