package net.louage.bijoux.sqlite;

import java.util.ArrayList;
import java.util.Date;

import net.louage.bijoux.constants.DateTime;
import net.louage.bijoux.model.Country;
import net.louage.bijoux.model.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SchemaHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "SQLiteCarpoolBijoux";
	private String path;
	private SQLiteDatabase myDataBase;

	// TODO Create child class of SchemaHelper for each object
	// TODO Interface for all crud methods to implement polymorphism
	// E.g.: private tours, public tours, ... trough interfacing...
	// Search for example Interface ->Abstract class -> implementation sqlite
	// with multiple tables

	// TOGGLE THIS NUMBER FOR UPDATING TABLES AND DATABASE
	private static final int DATABASE_VERSION = 7;

	public SchemaHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// Log.v("Schemahelper constructor", "Step 1");
		path = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create Team Table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TeamTable.TABLE_NAME + " ("
				+ TeamTable.ID + " INTEGER  NOT NULL PRIMARY KEY,"
				+ TeamTable.TEAMNAME + " TEXT NOT NULL);");
		
		/*// Create User_Team Table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + UserTeamTable.TABLE_NAME + " ("
				+ UserTeamTable.USER_ID + " INTEGER  NOT NULL PRIMARY KEY,"
				+ UserTeamTable.TEAM_ID + " TEXT NOT NULL,"
				+"PRIMARY KEY ("+UserTeamTable.USER_ID+","+UserTeamTable.TEAM_ID+"));");*/
		
		// Create User Table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + UserTable.TABLE_NAME + " ("
				+ UserTable.ID + " INTEGER  NOT NULL PRIMARY KEY,"
				+ UserTable.ACTIVATION + " DATE,"
				+ UserTable.LASTNAME + " TEXT NOT NULL,"
				+ UserTable.FIRSTNAME + " TEXT NOT NULL,"
				+ UserTable.EMAIL + " TEXT NOT NULL,"
				+ UserTable.PHONE + " TEXT,"
				+ UserTable.INFO + " TEXT,"
				+ UserTable.UPDATE_AT + " TIMESTAMP,"
				//+ " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,"
				+ UserTable.DRIVERLICENSE + " TEXT  NULL,"
				+ UserTable.APPROVED + " BOOLEAN);");
				/*+ " CREATE TRIGGER user_upd_datetime AFTER UPDATE ON "
				+ UserTable.TABLE_NAME + "  BEGIN UPDATE "
				+ UserTable.TABLE_NAME + " SET " + UserTable.UPDATE_AT
				+ " datetime('now') WHERE  " + UserTable.ID + "= new"
				+ UserTable.ID + "; END;");*/
		// Create Vehicle Table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + VehicleTable.TABLE_NAME
				+ " (" + VehicleTable.ID + " INTEGER  NOT NULL PRIMARY KEY,"
				+ VehicleTable.LICENSEPLATE + " TEXT  NULL,"
				+ VehicleTable.COUNTRY + " TEXT  NULL,"
				+ VehicleTable.NUMBEROFPASSENGERS + " INTEGER  NULL"
				+ VehicleTable.BRAND + " TEXT  NULL,"
				+ VehicleTable.VEHICLE_TYPE + " TEXT  NULL,"
				+ VehicleTable.USER_ID + " INTEGER  NULL,"
				+ VehicleTable.UPDATE_AT
				+ " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL);"
				+ " CREATE TRIGGER vehicle_upd_datetime AFTER UPDATE ON "
				+ VehicleTable.TABLE_NAME + "  BEGIN UPDATE "
				+ VehicleTable.TABLE_NAME + " SET " + VehicleTable.UPDATE_AT
				+ " datetime('now') WHERE  " + VehicleTable.ID + "= new"
				+ VehicleTable.ID + "; END;");
		// Create Tour Table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TourTable.TABLE_NAME + " ("
				+ TourTable.ID + " INTEGER  PRIMARY KEY NOT NULL,"
				+ TourTable.DATE + " DATE NULL," + TourTable.TIME
				+ " TIME NULL," + TourTable.USER_ID + " INTEGER NULL,"
				+ TourTable.VEHICLE_ID + " INTEGER NULL," + TourTable.UPDATE_AT
				+ " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL);"
				+ TourTable.TABLE_NAME + "  BEGIN UPDATE "
				+ TourTable.TABLE_NAME + " SET " + TourTable.UPDATE_AT
				+ " datetime('now') WHERE  " + TourTable.ID + "= new"
				+ TourTable.ID + "; END;");
		// Create Tracking Table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TrackingTable.TABLE_NAME
				+ " (" + TrackingTable.ID
				+ " INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"
				+ TrackingTable.TRACK_DATE_TIME
				+ " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,"
				+ TrackingTable.TOUR_ID + " INTEGER NOT NULL,"
				+ TrackingTable.LATITUDE + " TEXT  NOT NULL,"
				+ TrackingTable.LONGITUDE + " TEXT  NOT NULL,"
				+ TrackingTable.ACCURACY + " NUMERIC  NULL,"
				+ TrackingTable.ALTITUDE + " NUMERIC  NULL,"
				+ TrackingTable.SPEED + " NUMERIC  NULL,"
				+ TrackingTable.CLOUD_ID + " NUMERIC  NULL);"
				+ " CREATE TRIGGER tracking_create_datetime AFTER INSERT ON "
				+ TrackingTable.TABLE_NAME + "  BEGIN UPDATE "
				+ TrackingTable.TABLE_NAME + " SET "
				+ TrackingTable.TRACK_DATE_TIME + " datetime('now') WHERE  "
				+ TrackingTable.ID + "= new" + TrackingTable.ID + "; END;");

		// Create Country Table
		String countryQuery = "CREATE TABLE IF NOT EXISTS "
				+ CountryTable.TABLE_NAME + " (" + CountryTable.ID
				+ " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
				+ CountryTable.ISO + " TEXT NOT NULL, "
				+ CountryTable.DESCRIPTION + " TEXT  NULL); ";
		db.execSQL(countryQuery);
		Log.d("countryQuery", countryQuery);
		String countryInsertQuery = "INSERT INTO "
				+ CountryTable.TABLE_NAME
				+ " SELECT 1 AS "
				+ CountryTable.ID
				+ ", 'AD' AS "
				+ CountryTable.ISO
				+ ", 'Andorra' AS "
				+ CountryTable.DESCRIPTION
				+ " UNION SELECT  2, 'AE', 'United Arab Emirates'"
				+ " UNION SELECT  3, 'AF', 'Afghanistan'"
				+ " UNION SELECT  4, 'AG', 'Antigua and Barbuda'"
				+ " UNION SELECT  5, 'AI', 'Anguilla'"
				+ " UNION SELECT  6, 'AL', 'Albania'"
				+ " UNION SELECT  7, 'AM', 'Armenia'"
				+ " UNION SELECT  8, 'AO', 'Angola'"
				+ " UNION SELECT  9, 'AQ', 'Antarctica'"
				+ " UNION SELECT  10, 'AR', 'Argentina'"
				+ " UNION SELECT  11, 'AS', 'American Samoa'"
				+ " UNION SELECT  12, 'AT', 'Austria'"
				+ " UNION SELECT  13, 'AU', 'Australia'"
				+ " UNION SELECT  14, 'AW', 'Aruba'"
				+ " UNION SELECT  15, 'AX', 'Åland Islands'"
				+ " UNION SELECT  16, 'AZ', 'Azerbaijan'"
				+ " UNION SELECT  17, 'BA', 'Bosnia and Herzegovina'"
				+ " UNION SELECT  18, 'BB', 'Barbados'"
				+ " UNION SELECT  19, 'BD', 'Bangladesh'"
				+ " UNION SELECT  20, 'BE', 'Belgium'"
				+ " UNION SELECT  21, 'BF', 'Burkina Faso'"
				+ " UNION SELECT  22, 'BG', 'Bulgaria'"
				+ " UNION SELECT  23, 'BH', 'Bahrain'"
				+ " UNION SELECT  24, 'BI', 'Burundi'"
				+ " UNION SELECT  25, 'BJ', 'Benin'"
				+ " UNION SELECT  26, 'BL', 'Saint Barthélemy'"
				+ " UNION SELECT  27, 'BM', 'Bermuda'"
				+ " UNION SELECT  28, 'BN', 'Brunei Darussalam'"
				+ " UNION SELECT  29, 'BO', 'Bolivia, Plurinational State of'"
				+ " UNION SELECT  30, 'BQ', 'Bonaire, Sint Eustatius and Saba'"
				+ " UNION SELECT  31, 'BR', 'Brazil'"
				+ " UNION SELECT  32, 'BS', 'Bahamas'"
				+ " UNION SELECT  33, 'BT', 'Bhutan'"
				+ " UNION SELECT  34, 'BV', 'Bouvet Island'"
				+ " UNION SELECT  35, 'BW', 'Botswana'"
				+ " UNION SELECT  36, 'BY', 'Belarus'"
				+ " UNION SELECT  37, 'BZ', 'Belize'"
				+ " UNION SELECT  38, 'CA', 'Canada'"
				+ " UNION SELECT  39, 'CC', 'Cocos (Keeling) Islands'"
				+ " UNION SELECT  40, 'CD', 'Congo, the Democratic Republic'"
				+ " UNION SELECT  41, 'CF', 'Central African Republic'"
				+ " UNION SELECT  42, 'CG', 'Congo'"
				+ " UNION SELECT  43, 'CH', 'Switzerland'"
				+ " UNION SELECT  44, 'CI', 'Côte d''Ivoire'"
				+ " UNION SELECT  45, 'CK', 'Cook Islands'"
				+ " UNION SELECT  46, 'CL', 'Chile'"
				+ " UNION SELECT  47, 'CM', 'Cameroon'"
				+ " UNION SELECT  48, 'CN', 'China'"
				+ " UNION SELECT  49, 'CO', 'Colombia'"
				+ " UNION SELECT  50, 'CR', 'Costa Rica'"
				+ " UNION SELECT  51, 'CU', 'Cuba'"
				+ " UNION SELECT  52, 'CV', 'Cabo Verde'"
				+ " UNION SELECT  53, 'CW', 'Curaçao'"
				+ " UNION SELECT  54, 'CX', 'Christmas Island'"
				+ " UNION SELECT  55, 'CY', 'Cyprus'"
				+ " UNION SELECT  56, 'CZ', 'Czech Republic'"
				+ " UNION SELECT  57, 'DE', 'Germany'"
				+ " UNION SELECT  58, 'DJ', 'Djibouti'"
				+ " UNION SELECT  59, 'DK', 'Denmark'"
				+ " UNION SELECT  60, 'DM', 'Dominica'"
				+ " UNION SELECT  61, 'DO', 'Dominican Republic'"
				+ " UNION SELECT  62, 'DZ', 'Algeria'"
				+ " UNION SELECT  63, 'EC', 'Ecuador'"
				+ " UNION SELECT  64, 'EE', 'Estonia'"
				+ " UNION SELECT  65, 'EG', 'Egypt'"
				+ " UNION SELECT  66, 'EH', 'Western Sahara'"
				+ " UNION SELECT  67, 'ER', 'Eritrea'"
				+ " UNION SELECT  68, 'ES', 'Spain'"
				+ " UNION SELECT  69, 'ET', 'Ethiopia'"
				+ " UNION SELECT  70, 'FI', 'Finland'"
				+ " UNION SELECT  71, 'FJ', 'Fiji'"
				+ " UNION SELECT  72, 'FK', 'Falkland Islands (Malvinas)'"
				+ " UNION SELECT  73, 'FM', 'Micronesia, Federated States of'"
				+ " UNION SELECT  74, 'FO', 'Faroe Islands'"
				+ " UNION SELECT  75, 'FR', 'France'"
				+ " UNION SELECT  76, 'GA', 'Gabon'"
				+ " UNION SELECT  77, 'GB', 'United Kingdom'"
				+ " UNION SELECT  78, 'GD', 'Grenada'"
				+ " UNION SELECT  79, 'GE', 'Georgia'"
				+ " UNION SELECT  80, 'GF', 'French Guiana'"
				+ " UNION SELECT  81, 'GG', 'Guernsey'"
				+ " UNION SELECT  82, 'GH', 'Ghana'"
				+ " UNION SELECT  83, 'GI', 'Gibraltar'"
				+ " UNION SELECT  84, 'GL', 'Greenland'"
				+ " UNION SELECT  85, 'GM', 'Gambia'"
				+ " UNION SELECT  86, 'GN', 'Guinea'"
				+ " UNION SELECT  87, 'GP', 'Guadeloupe'"
				+ " UNION SELECT  88, 'GQ', 'Equatorial Guinea'"
				+ " UNION SELECT  89, 'GR', 'Greece'"
				+ " UNION SELECT  90, 'GS', 'South Georgia and the South Sandwich Islands'"
				+ " UNION SELECT  91, 'GT', 'Guatemala'"
				+ " UNION SELECT  92, 'GU', 'Guam'"
				+ " UNION SELECT  93, 'GW', 'Guinea-Bissau'"
				+ " UNION SELECT  94, 'GY', 'Guyana'"
				+ " UNION SELECT  95, 'HK', 'Hong Kong'"
				+ " UNION SELECT  96, 'HM', 'Heard Island and McDonald Islands'"
				+ " UNION SELECT  97, 'HN', 'Honduras'"
				+ " UNION SELECT  98, 'HR', 'Croatia'"
				+ " UNION SELECT  99, 'HT', 'Haiti'"
				+ " UNION SELECT  100, 'HU', 'Hungary'"
				+ " UNION SELECT  101, 'ID', 'Indonesia'"
				+ " UNION SELECT  102, 'IE', 'Ireland'"
				+ " UNION SELECT  103, 'IL', 'Israel'"
				+ " UNION SELECT  104, 'IM', 'Isle of Man'"
				+ " UNION SELECT  105, 'IN', 'India'"
				+ " UNION SELECT  106, 'IO', 'British Indian Ocean Territory'"
				+ " UNION SELECT  107, 'IQ', 'Iraq'"
				+ " UNION SELECT  108, 'IR', 'Iran, Islamic Republic of'"
				+ " UNION SELECT  109, 'IS', 'Iceland'"
				+ " UNION SELECT  110, 'IT', 'Italy'"
				+ " UNION SELECT  111, 'JE', 'Jersey'"
				+ " UNION SELECT  112, 'JM', 'Jamaica'"
				+ " UNION SELECT  113, 'JO', 'Jordan'"
				+ " UNION SELECT  114, 'JP', 'Japan'"
				+ " UNION SELECT  115, 'KE', 'Kenya'"
				+ " UNION SELECT  116, 'KG', 'Kyrgyzstan'"
				+ " UNION SELECT  117, 'KH', 'Cambodia'"
				+ " UNION SELECT  118, 'KI', 'Kiribati'"
				+ " UNION SELECT  119, 'KM', 'Comoros'"
				+ " UNION SELECT  120, 'KN', 'Saint Kitts and Nevis'"
				+ " UNION SELECT  121, 'KP', 'Korea, Democratic People''s Republic of'"
				+ " UNION SELECT  122, 'KR', 'Korea, Republic of'"
				+ " UNION SELECT  123, 'KW', 'Kuwait'"
				+ " UNION SELECT  124, 'KY', 'Cayman Islands'"
				+ " UNION SELECT  125, 'KZ', 'Kazakhstan'"
				+ " UNION SELECT  126, 'LA', 'Lao People''s Democratic Republic'"
				+ " UNION SELECT  127, 'LB', 'Lebanon'"
				+ " UNION SELECT  128, 'LC', 'Saint Lucia'"
				+ " UNION SELECT  129, 'LI', 'Liechtenstein'"
				+ " UNION SELECT  130, 'LK', 'Sri Lanka'"
				+ " UNION SELECT  131, 'LR', 'Liberia'"
				+ " UNION SELECT  132, 'LS', 'Lesotho'"
				+ " UNION SELECT  133, 'LT', 'Lithuania'"
				+ " UNION SELECT  134, 'LU', 'Luxembourg'"
				+ " UNION SELECT  135, 'LV', 'Latvia'"
				+ " UNION SELECT  136, 'LY', 'Libya'"
				+ " UNION SELECT  137, 'MA', 'Morocco'"
				+ " UNION SELECT  138, 'MC', 'Monaco'"
				+ " UNION SELECT  139, 'MD', 'Moldova, Republic of'"
				+ " UNION SELECT  140, 'ME', 'Montenegro'"
				+ " UNION SELECT  141, 'MF', 'Saint Martin (French part)'"
				+ " UNION SELECT  142, 'MG', 'Madagascar'"
				+ " UNION SELECT  143, 'MH', 'Marshall Islands'"
				+ " UNION SELECT  144, 'MK', 'Macedonia, the former Yugoslav Republic of'"
				+ " UNION SELECT  145, 'ML', 'Mali'"
				+ " UNION SELECT  146, 'MM', 'Myanmar'"
				+ " UNION SELECT  147, 'MN', 'Mongolia'"
				+ " UNION SELECT  148, 'MO', 'Macao'"
				+ " UNION SELECT  149, 'MP', 'Northern Mariana Islands'"
				+ " UNION SELECT  150, 'MQ', 'Martinique'"
				+ " UNION SELECT  151, 'MR', 'Mauritania'"
				+ " UNION SELECT  152, 'MS', 'Montserrat'"
				+ " UNION SELECT  153, 'MT', 'Malta'"
				+ " UNION SELECT  154, 'MU', 'Mauritius'"
				+ " UNION SELECT  155, 'MV', 'Maldives'"
				+ " UNION SELECT  156, 'MW', 'Malawi'"
				+ " UNION SELECT  157, 'MX', 'Mexico'"
				+ " UNION SELECT  158, 'MY', 'Malaysia'"
				+ " UNION SELECT  159, 'MZ', 'Mozambique'"
				+ " UNION SELECT  160, 'NA', 'Namibia'"
				+ " UNION SELECT  161, 'NC', 'New Caledonia'"
				+ " UNION SELECT  162, 'NE', 'Niger'"
				+ " UNION SELECT  163, 'NF', 'Norfolk Island'"
				+ " UNION SELECT  164, 'NG', 'Nigeria'"
				+ " UNION SELECT  165, 'NI', 'Nicaragua'"
				+ " UNION SELECT  166, 'NL', 'Netherlands'"
				+ " UNION SELECT  167, 'NO', 'Norway'"
				+ " UNION SELECT  168, 'NP', 'Nepal'"
				+ " UNION SELECT  169, 'NR', 'Nauru'"
				+ " UNION SELECT  170, 'NU', 'Niue'"
				+ " UNION SELECT  171, 'NZ', 'New Zealand'"
				+ " UNION SELECT  172, 'OM', 'Oman'"
				+ " UNION SELECT  173, 'PA', 'Panama'"
				+ " UNION SELECT  174, 'PE', 'Peru'"
				+ " UNION SELECT  175, 'PF', 'French Polynesia'"
				+ " UNION SELECT  176, 'PG', 'Papua New Guinea'"
				+ " UNION SELECT  177, 'PH', 'Philippines'"
				+ " UNION SELECT  178, 'PK', 'Pakistan'"
				+ " UNION SELECT  179, 'PL', 'Poland'"
				+ " UNION SELECT  180, 'PM', 'Saint Pierre and Miquelon'"
				+ " UNION SELECT  181, 'PN', 'Pitcairn'"
				+ " UNION SELECT  182, 'PR', 'Puerto Rico'"
				+ " UNION SELECT  183, 'PS', 'Palestine, State of'"
				+ " UNION SELECT  184, 'PT', 'Portugal'"
				+ " UNION SELECT  185, 'PW', 'Palau'"
				+ " UNION SELECT  186, 'PY', 'Paraguay'"
				+ " UNION SELECT  187, 'QA', 'Qatar'"
				+ " UNION SELECT  188, 'RE', 'Réunion'"
				+ " UNION SELECT  189, 'RO', 'Romania'"
				+ " UNION SELECT  190, 'RS', 'Serbia'"
				+ " UNION SELECT  191, 'RU', 'Russian Federation'"
				+ " UNION SELECT  192, 'RW', 'Rwanda'"
				+ " UNION SELECT  193, 'SA', 'Saudi Arabia'"
				+ " UNION SELECT  194, 'SB', 'Solomon Islands'"
				+ " UNION SELECT  195, 'SC', 'Seychelles'"
				+ " UNION SELECT  196, 'SD', 'Sudan'"
				+ " UNION SELECT  197, 'SE', 'Sweden'"
				+ " UNION SELECT  198, 'SG', 'Singapore'"
				+ " UNION SELECT  199, 'SH', 'Saint Helena, Ascension and Tristan da Cunha'"
				+ " UNION SELECT  200, 'SI', 'Slovenia'"
				+ " UNION SELECT  201, 'SJ', 'Svalbard and Jan Mayen'"
				+ " UNION SELECT  202, 'SK', 'Slovakia'"
				+ " UNION SELECT  203, 'SL', 'Sierra Leone'"
				+ " UNION SELECT  204, 'SM', 'San Marino'"
				+ " UNION SELECT  205, 'SN', 'Senegal'"
				+ " UNION SELECT  206, 'SO', 'Somalia'"
				+ " UNION SELECT  207, 'SR', 'Suriname'"
				+ " UNION SELECT  208, 'SS', 'South Sudan'"
				+ " UNION SELECT  209, 'ST', 'Sao Tome and Principe'"
				+ " UNION SELECT  210, 'SV', 'El Salvador'"
				+ " UNION SELECT  211, 'SX', 'Sint Maarten (Dutch part)'"
				+ " UNION SELECT  212, 'SY', 'Syrian Arab Republic'"
				+ " UNION SELECT  213, 'SZ', 'Swaziland'"
				+ " UNION SELECT  214, 'TC', 'Turks and Caicos Islands'"
				+ " UNION SELECT  215, 'TD', 'Chad'"
				+ " UNION SELECT  216, 'TF', 'French Southern Territories'"
				+ " UNION SELECT  217, 'TG', 'Togo'"
				+ " UNION SELECT  218, 'TH', 'Thailand'"
				+ " UNION SELECT  219, 'TJ', 'Tajikistan'"
				+ " UNION SELECT  220, 'TK', 'Tokelau'"
				+ " UNION SELECT  221, 'TL', 'Timor-Leste'"
				+ " UNION SELECT  222, 'TM', 'Turkmenistan'"
				+ " UNION SELECT  223, 'TN', 'Tunisia'"
				+ " UNION SELECT  224, 'TO', 'Tonga'"
				+ " UNION SELECT  225, 'TR', 'Turkey'"
				+ " UNION SELECT  226, 'TT', 'Trinidad and Tobago'"
				+ " UNION SELECT  227, 'TV', 'Tuvalu'"
				+ " UNION SELECT  228, 'TW', 'Taiwan, Province of China'"
				+ " UNION SELECT  229, 'TZ', 'Tanzania, United Republic of'"
				+ " UNION SELECT  230, 'UA', 'Ukraine'"
				+ " UNION SELECT  231, 'UG', 'Uganda'"
				+ " UNION SELECT  232, 'UM', 'United States Minor Outlying Islands'"
				+ " UNION SELECT  233, 'US', 'United States'"
				+ " UNION SELECT  234, 'UY', 'Uruguay'"
				+ " UNION SELECT  235, 'UZ', 'Uzbekistan'"
				+ " UNION SELECT  236, 'VA', 'Holy See (Vatican City State)'"
				+ " UNION SELECT  237, 'VC', 'Saint Vincent and the Grenadines'"
				+ " UNION SELECT  238, 'VE', 'Venezuela, Bolivarian Republic of'"
				+ " UNION SELECT  239, 'VG', 'Virgin Islands, British'"
				+ " UNION SELECT  240, 'VI', 'Virgin Islands, U.S.'"
				+ " UNION SELECT  241, 'VN', 'Viet Nam'"
				+ " UNION SELECT  242, 'VU', 'Vanuatu'"
				+ " UNION SELECT  243, 'WF', 'Wallis and Futuna'"
				+ " UNION SELECT  244, 'WS', 'Samoa'"
				+ " UNION SELECT  245, 'YE', 'Yemen'"
				+ " UNION SELECT  246, 'YT', 'Mayotte'"
				+ " UNION SELECT  247, 'ZA', 'South Africa'"
				+ " UNION SELECT  248, 'ZM', 'Zambia'"
				+ " UNION SELECT  249, 'ZW', 'Zimbabwe'";
		db.execSQL(countryInsertQuery);
		Log.d("countryInsertQuery", countryInsertQuery);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("LOG_TAG", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + UserTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + VehicleTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TourTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TrackingTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + CountryTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TeamTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + UserTeamTable.TABLE_NAME);
		onCreate(db);

	}

	public boolean openDataBase() {
		// String myPath = path + databaseName;
		// Log.d(tag, "path = "+myPath);
		try {
			myDataBase = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY
							| SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		} catch (SQLException sqle) {
			myDataBase = null;
			// Log.d(tag, "exception in openDataBase()");
			return false;
		}
		// Log.d(tag, "returning true from openDataBase()");
		return true;

	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	public Boolean userCreateOrUpdate(ArrayList<User> usrs) {
		SQLiteDatabase sd = getWritableDatabase();
		//String tag="SchemaHelper createOrUpdateUser";

		for (int i = 0; i < usrs.size(); i++) {
			User usr = usrs.get(i);
			Boolean sqLiteUser = checkIfUserExists(usr.getUser_id());
			//Log.d(tag, "checkIfUserExists: "+sqLiteUser+" (for user "+usr.getLastname()+" "+usr.getFirstname()+")");
			if (sqLiteUser == true) {
				// update user
				String sqLiteDate = DateTime.getStrSQLiteDateStamp(usr.getActivation());
				ContentValues cv = new ContentValues();
				cv.put(UserTable.ID, usr.getUser_id());
				cv.put(UserTable.ACTIVATION, sqLiteDate);
				cv.put(UserTable.LASTNAME, usr.getLastname());
				cv.put(UserTable.FIRSTNAME, usr.getFirstname());
				cv.put(UserTable.EMAIL, usr.getEmail());
				cv.put(UserTable.PHONE, usr.getPhone());
				cv.put(UserTable.INFO, usr.getInfo());
				String sqLiteUpdatedDate = DateTime.getStrSQLiteDateTimeStamp(usr.getUpdate_at());
				cv.put(UserTable.UPDATE_AT, sqLiteUpdatedDate);
				cv.put(UserTable.DRIVERLICENSE, usr.getDriverlicense());
				Boolean approved = usr.getApproved();
				if (approved==true) {
					cv.put(UserTable.APPROVED, 1);
				} else {
					cv.put(UserTable.APPROVED, 0);
				}
				
				String whereClause = UserTable.ID + "=?";
				String[] whereArgs = { Integer.toString(usr.getUser_id()) };
				sd.update(UserTable.TABLE_NAME, cv, whereClause, whereArgs);
			} else {
				// create user
				String sqLiteDate = DateTime.getStrSQLiteDateStamp(usr.getActivation());
				ContentValues cv = new ContentValues();
				cv.put(UserTable.ID, usr.getUser_id());
				cv.put(UserTable.ACTIVATION, sqLiteDate);
				cv.put(UserTable.LASTNAME, usr.getLastname());
				cv.put(UserTable.FIRSTNAME, usr.getFirstname());
				cv.put(UserTable.EMAIL, usr.getEmail());
				cv.put(UserTable.PHONE, usr.getPhone());
				cv.put(UserTable.INFO, usr.getInfo());
				String sqLiteUpdatedDate = DateTime.getStrSQLiteDateTimeStamp(usr.getUpdate_at());
				cv.put(UserTable.UPDATE_AT, sqLiteUpdatedDate);
				cv.put(UserTable.DRIVERLICENSE, usr.getDriverlicense());
				Boolean approved = usr.getApproved();
				if (approved==true) {
					cv.put(UserTable.APPROVED, 1);
				} else {
					cv.put(UserTable.APPROVED, 0);
				}
				sd.insert(UserTable.TABLE_NAME, null, cv);
			}
		}
		return true;
	}
	
	public ArrayList<User> getUsers() {
		//String tag="SchemaHelper getUsers";
		SQLiteDatabase sd = getWritableDatabase();
		ArrayList<User>users=new ArrayList<User>();
		String selection = UserTable.APPROVED+"=?"; 
		String[] selectionArgs = new String[] {Integer.toString(1)};
		Cursor c = sd.query(UserTable.TABLE_NAME, null, selection, selectionArgs, null, null, null);
		while (c.moveToNext()) {
			User usr = new User();
			usr.setUser_id(c.getInt(c.getColumnIndex(UserTable.ID)));
			Date activationDate=DateTime.getDateSQLiteString(c.getString(c.getColumnIndex(UserTable.ACTIVATION)));
			usr.setActivation(activationDate);
			usr.setLastname(c.getString(c.getColumnIndex(UserTable.LASTNAME)));
			usr.setFirstname(c.getString(c.getColumnIndex(UserTable.FIRSTNAME)));
			usr.setEmail(c.getString(c.getColumnIndex(UserTable.EMAIL)));
			usr.setPhone(c.getString(c.getColumnIndex(UserTable.PHONE)));
			usr.setInfo(c.getString(c.getColumnIndex(UserTable.INFO)));
			Date updateDate=DateTime.getDateTimeSQLiteString(c.getString(c.getColumnIndex(UserTable.UPDATE_AT)));
			usr.setUpdate_at(updateDate);
			usr.setDriverlicense(c.getString(c.getColumnIndex(UserTable.DRIVERLICENSE)));
			int approved = c.getInt(c.getColumnIndex(UserTable.APPROVED));
			if (approved==1) {
				usr.setApproved(true);
			} else {
				usr.setApproved(false);
			}
			
			users.add(usr);
			//Log.d(tag, "User: "+usr.getLastname()+" "+usr.getFirstname()+" was added to ArrayList users");
		}
		return users;
	}
	
	public ArrayList<User> getUsersToApprove() {
		//String tag="SchemaHelper getUsers";
		SQLiteDatabase sd = getWritableDatabase();
		ArrayList<User>users=new ArrayList<User>();
		String selection = UserTable.APPROVED+"=?";
		String[] selectionArgs = new String[] {Integer.toString(0)};
		Cursor c = sd.query(UserTable.TABLE_NAME, null, selection, selectionArgs, null, null, null);
		while (c.moveToNext()) {
			User usr = new User();
			usr.setUser_id(c.getInt(c.getColumnIndex(UserTable.ID)));
			Date activationDate=DateTime.getDateSQLiteString(c.getString(c.getColumnIndex(UserTable.ACTIVATION)));
			usr.setActivation(activationDate);
			usr.setLastname(c.getString(c.getColumnIndex(UserTable.LASTNAME)));
			usr.setFirstname(c.getString(c.getColumnIndex(UserTable.FIRSTNAME)));
			usr.setEmail(c.getString(c.getColumnIndex(UserTable.EMAIL)));
			usr.setPhone(c.getString(c.getColumnIndex(UserTable.PHONE)));
			usr.setInfo(c.getString(c.getColumnIndex(UserTable.INFO)));
			Date updateDate=DateTime.getDateTimeSQLiteString(c.getString(c.getColumnIndex(UserTable.UPDATE_AT)));
			usr.setUpdate_at(updateDate);
			usr.setDriverlicense(c.getString(c.getColumnIndex(UserTable.DRIVERLICENSE)));
			int approved = c.getInt(c.getColumnIndex(UserTable.APPROVED));
			if (approved==1) {
				usr.setApproved(true);
			} else {
				usr.setApproved(false);
			}
			
			users.add(usr);
			//Log.d(tag, "User: "+usr.getLastname()+" "+usr.getFirstname()+" was added to ArrayList users");
		}
		return users;
	}
	
	public int userDelete(int user_id){
		SQLiteDatabase sd = getWritableDatabase();
		//String tag="SchemaHelper userDelete";
		String table = UserTable.TABLE_NAME;
		String whereClause = "_id"+"=?";
		String[]whereArgs = new String[] {String.valueOf(user_id)};
		int noRowsAffected=sd.delete(table, whereClause , whereArgs);
		if (noRowsAffected>0) {
			return user_id;
		} else {
			return -1;
		}
	}

	private Boolean checkIfUserExists(int user_id) {
		String tag="SchemaHelper checkIfUserExists";
		SQLiteDatabase sd = getWritableDatabase();
		String[] columns = new String[] { String.valueOf(user_id) };
		Log.d(tag, "columns: "+ String.valueOf(user_id));
		String selection = UserTable.ID+ "= ? ";
		Log.d(tag, "selection: "+ selection);
		String[] selectionArgs = new String[] { String.valueOf(user_id) };
		Log.d(tag, "selectionArgs: "+ String.valueOf(user_id));
		Cursor c = sd.query(UserTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
		if (c.moveToFirst()) {
			return true;
		} else {
			return false;
		}
	}

	public Cursor getCountries() {
		SQLiteDatabase sd = getWritableDatabase();
		// Define the columns we want to get from SQLite
		String[] columns = new String[] { CountryTable.ID, CountryTable.ISO,
				CountryTable.DESCRIPTION };
		// There are no selection arguments
		// String[] selectionArgs = new String[] { };
		Cursor c = sd.query(CountryTable.TABLE_NAME, columns, null, null, null,
				null, null);
		return c;
	}

	public Cursor getCountryDescription(String iso3166) {
		SQLiteDatabase sd = getWritableDatabase();
		String[] columns = new String[] { CountryTable.ID, CountryTable.ISO,
				CountryTable.DESCRIPTION };
		String[] selectionArgs = new String[] { iso3166 };
		Cursor c = sd.query(CountryTable.TABLE_NAME, columns, CountryTable.ISO
				+ "= ? ", selectionArgs, null, null, null);
		return c;
	}

	public Country getCountry(String description) {
		SQLiteDatabase sd = getWritableDatabase();
		String[] columns = new String[] { CountryTable.ID, CountryTable.ISO,
				CountryTable.DESCRIPTION };
		String[] selectionArgs = new String[] { description };
		Cursor c = sd.query(CountryTable.TABLE_NAME, columns,
				CountryTable.DESCRIPTION + "= ? ", selectionArgs, null, null,
				null);
		Country country = new Country();
		while (c.moveToNext()) {
			country.setId(c.getInt(c.getColumnIndex(CountryTable.ID)));
			country.setDescription(c.getString(c
					.getColumnIndex(CountryTable.DESCRIPTION)));
			country.setIso3166(c.getString(c.getColumnIndex(CountryTable.ISO)));
		}
		return country;
	}

	public Country getCountryFromIso3166(String iso3166) {
		SQLiteDatabase sd = getWritableDatabase();
		String[] columns = new String[] { CountryTable.ID, CountryTable.ISO,
				CountryTable.DESCRIPTION };
		String[] selectionArgs = new String[] { iso3166 };
		Cursor c = sd.query(CountryTable.TABLE_NAME, columns, CountryTable.ISO
				+ "= ? ", selectionArgs, null, null, null);
		Country country = new Country();
		while (c.moveToNext()) {
			country.setId(c.getInt(c.getColumnIndex(CountryTable.ID)));
			country.setDescription(c.getString(c
					.getColumnIndex(CountryTable.DESCRIPTION)));
			country.setIso3166(c.getString(c.getColumnIndex(CountryTable.ISO)));
		}
		return country;
	}

}
