	/*************************************************************************
	 *  Compilation: javac CNIGen
	 *  Execution:    java CNIGen 
	 *  Author: Pericles Oliveira
	 *  
	 *  Remark: this is, perhaps, easier by counting from 0 to 2^N - 1 by 1
	 *  and looking at the bit representation of the counter. However, this
	 *  recursive approach generalizes easily, e.g., if you want to print
	 *  out all combinations of size k.
	 *
	 *************************************************************************/
	import java.util.LinkedList;
	import java.util.Stack;
	import java.util.ArrayDeque;
	import java.util.Deque;
	import java.util.Queue;
	import java.util.PriorityQueue;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.List;
	import java.util.StringTokenizer;
	import java.util.HashMap;
	import java.util.Iterator;
	import java.util.Properties;
	import java.io.IOException;
	import java.io.PrintWriter;
	import java.util.regex.Pattern;
	import java.util.HashSet;
	import java.util.Set;
	import java.util.Vector;
	import java.util.Map;
	import java.util.SortedMap;
	import java.util.TreeMap;
	import java.util.Collection;
	import java.util.Collections;

	import java.io.IOException;
	import java.io.UnsupportedEncodingException;
	import java.net.URL;


	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.ResultSet;
	import java.sql.ResultSetMetaData;
	import java.sql.SQLException;
	import java.sql.Statement;
	import java.io.FileInputStream;
	import java.io.InputStreamReader;
	import java.io.BufferedReader;

	import java.lang.Math;

	public class ModCNGen {
	    private static Integer nroCNs=0;
	    private static ArrayList<ArrayList<String>> terminalSets 			= new ArrayList<ArrayList<String>>();
	    private static ArrayList<String> SQLs					= new ArrayList<String>();
	    private static HashMap<String,HashMap<String,Integer>> termIndexTuples  	= new HashMap<String, HashMap<String,Integer>>();
	    private static HashMap<String,HashMap<Integer,Integer>> termIndex          	= new HashMap<String, HashMap<Integer,Integer>>();
	    private static List<List<String>>           distributions			= new ArrayList<List<String>>();
	    private static Stack<String> 		path				= new Stack<String>();   // the current path
	    private static ArrayList<ArrayList<String>> steinerTrees    		= new ArrayList<ArrayList<String>>(); // list of steiner tree
	    private static Stack<String> 		shortPath			= new Stack<String>(); // the shortest path
	    private static SET<String> 			onPath				= new SET<String>();     // the set of vertices on the path
	    private static HashSet		        m_Words 			= new HashSet();
	    private static HashMap<Object,Integer> s=new HashMap<Object,Integer>();
	    private static HashMap<Object,Integer> m=new HashMap<Object,Integer>();
	    private static HashMap<Integer,String>  q=new HashMap<Integer,String>();
	    private static HashMap<Integer,String> map2attribute			= new HashMap<Integer,String>();      
	    private static HashMap<Integer,HashMap<String,Integer>>  distinctAttrbutes	= new HashMap<Integer,HashMap<String,Integer>>(); 
	    private static HashMap<String,Integer> attribute2map		        = new HashMap<String,Integer>(); 
	    private static ArrayList<String> eclatResult				= new ArrayList<String>();
	    private static Integer contadorTS=0;
	    private static HashMap<String,HashMap<String,String>> rGraph		= new HashMap<String,HashMap<String,String>>();
	    private static HashMap<String,List<String>> adjGraph			= new HashMap<String,List<String>>();
	    public static String[] tokenize(String value) { 
			Pattern SPLIT_PATTERN = Pattern.compile("([.,;:_ /#@!?~`|\"'{})(*&^%$])+");
			return SPLIT_PATTERN.split(value.toLowerCase());
	    }

	    /* =====================================================================================
	    *  getDBConnection
	    *  Description: function get one connection point to one remote dbms
	    *  created by: Pericles Oliveira
	    *  =====================================================================================
	   */
	    public static Connection getDBConnection(String driverClass, String driverName, String servername, String port,String database, String username, String password)
	    {
	       try {
		    // org.postgresql.Driver
		    Class.forName(driverClass);
		} catch (ClassNotFoundException cnfe) {
		    System.err.println("Couldn't find driver class:");
		    System.out.println("Couldn't find driver class:");
		    cnfe.printStackTrace();
		}
		Connection conn = null;
		try {
		    String url  = "jdbc:"+driverName+ "://"+servername+":"+port+"/"+database;
		    conn = DriverManager.getConnection(url , username, password);
		} catch (SQLException se) {
		    System.out.println("Couldn't connect: print out a stack trace and exit.");
		    se.printStackTrace();
		    System.exit(1);
		}

		if (conn != null)
		    System.out.println("Successfully connected to the Database");
		else
		    System.out.println("We should never get here.");

		return conn;
	    }

	    public static void genTokenCoOccurrence(ArrayList <String> s, ArrayList <String> copia) {
	      //tokensSet.clear();
	      genTokenCoOccurrence("", s,copia);
	    }
	    private static void genTokenCoOccurrence(String prefix, ArrayList <String> s, ArrayList <String> copia) {
		String prefix2 = new String();
		ArrayList<String> list = new ArrayList<String>();
		if(prefix.length()>0)
		{
		   prefix2=prefix+" ";
		   StringTokenizer st = new StringTokenizer(prefix2+bloco(prefix,copia));
		   while (st.hasMoreTokens()) {
		     list.add(st.nextToken());
		   }
		   //tokensSet.m_Words.add(list);
		}
		for (int i = 0; i < s.size(); i++)
		{
		    ArrayList<String> bloco = new ArrayList<String>();
		    for(int z=i+1;z<s.size();z++)
		      bloco.add(s.get(z));
		    genTokenCoOccurrence(new String(prefix + s.get(i)).trim(), bloco,copia);

		}
	    }

	    private static String bloco(String prefix, ArrayList <String> terms) {
	       String block = new String();
	       for(int i=0;i<terms.size();i++)
	       {
		 if(!prefix.contains(terms.get(i)))
		    block+=terms.get(i)+" ";
	       }
	       return block;
	    }

	    /**
	    * Returns true if the given string is a stop word.
	    * 
	    * @param word
	    *            the word to test
	    * @return true if the word is a stopword
	    */
	    public static boolean is(String word) {
		return m_Words.contains(word.toLowerCase());
	    }
	  
	    public static void startStopwords() {

			// Stopwords list from Rainbow
			m_Words.add("a");
			m_Words.add("able");
			m_Words.add("about");
			m_Words.add("the");
			m_Words.add("above");
			m_Words.add("according");
			m_Words.add("accordingly");
			m_Words.add("across");
			m_Words.add("actually");
			m_Words.add("after");
			m_Words.add("afterwards");
			m_Words.add("again");
			m_Words.add("against");
			m_Words.add("all");
			m_Words.add("allow");
			m_Words.add("allows");
			m_Words.add("almost");
			m_Words.add("alone");
			m_Words.add("along");
			m_Words.add("already");
			m_Words.add("also");
			m_Words.add("although");
			m_Words.add("always");
			m_Words.add("am");
			m_Words.add("among");
			m_Words.add("amongst");
			m_Words.add("an");
			m_Words.add("and");
			m_Words.add("another");
			m_Words.add("any");
			m_Words.add("anybody");
			m_Words.add("anyhow");
			m_Words.add("anyone");
			m_Words.add("are");
			m_Words.add("around");
			m_Words.add("as");
			m_Words.add("aside");
			m_Words.add("ask");
			m_Words.add("asking");
			m_Words.add("at");
			m_Words.add("away");
			m_Words.add("be");
			m_Words.add("became");
			m_Words.add("because");
			m_Words.add("become");
			m_Words.add("becomes");
			m_Words.add("becoming");
			m_Words.add("been");
			m_Words.add("before");
			m_Words.add("beforehand");
			m_Words.add("behind");
			m_Words.add("being");
			m_Words.add("believe");
			m_Words.add("below");
			m_Words.add("beside");
			m_Words.add("besides");
			m_Words.add("best");
			m_Words.add("better");
			m_Words.add("between");
			m_Words.add("beyond");
			m_Words.add("both");
			m_Words.add("brief");
			m_Words.add("but");
			m_Words.add("by");
			m_Words.add("c");
			m_Words.add("came");
			m_Words.add("can");
			m_Words.add("cannot");
			m_Words.add("cant");
			m_Words.add("cause");
			m_Words.add("causes");
			m_Words.add("certain");
			m_Words.add("certainly");
			m_Words.add("changes");
			m_Words.add("clearly");
			m_Words.add("co");
			m_Words.add("com");
			m_Words.add("come");
			m_Words.add("comes");
			m_Words.add("concerning");
			m_Words.add("consequently");
			m_Words.add("consider");
			m_Words.add("considering");
			m_Words.add("contain");
			m_Words.add("containing");
			m_Words.add("contains");
			m_Words.add("corresponding");
			m_Words.add("could");
			m_Words.add("course");
			m_Words.add("currently");
			m_Words.add("d");
			m_Words.add("definitely");
			m_Words.add("described");
			m_Words.add("despite");
			m_Words.add("did");
			m_Words.add("different");
			m_Words.add("do");
			m_Words.add("does");
			m_Words.add("doing");
			m_Words.add("done");
			m_Words.add("down");
			m_Words.add("downwards");
			m_Words.add("during");
			m_Words.add("e");
			m_Words.add("each");
			m_Words.add("edu");
			m_Words.add("eg");
			m_Words.add("eight");
			m_Words.add("either");
			m_Words.add("else");
			m_Words.add("elsewhere");
			m_Words.add("enough");
			m_Words.add("entirely");
			m_Words.add("especially");
			m_Words.add("et");
			m_Words.add("etc");
			m_Words.add("even");
			m_Words.add("ever");
			m_Words.add("every");
			m_Words.add("everybody");
			m_Words.add("everyone");
			m_Words.add("everything");
			m_Words.add("everywhere");
			m_Words.add("ex");
			m_Words.add("exactly");
			m_Words.add("example");
			m_Words.add("except");
			m_Words.add("f");
			m_Words.add("far");
			m_Words.add("few");
			m_Words.add("fifth");
			m_Words.add("first");
			m_Words.add("five");
			m_Words.add("followed");
			m_Words.add("following");
			m_Words.add("follows");
			m_Words.add("for");
			m_Words.add("former");
			m_Words.add("formerly");
			m_Words.add("forth");
			m_Words.add("four");
			m_Words.add("from");
			m_Words.add("further");
			m_Words.add("furthermore");
			m_Words.add("g");
			m_Words.add("get");
			m_Words.add("gets");
			m_Words.add("getting");
			m_Words.add("given");
			m_Words.add("gives");
			m_Words.add("go");
			m_Words.add("goes");
			m_Words.add("going");
			m_Words.add("got");
			m_Words.add("gotten");
			m_Words.add("greetings");
			m_Words.add("h");
			m_Words.add("had");
			m_Words.add("happens");
			m_Words.add("hardly");
			m_Words.add("has");
			m_Words.add("have");
			m_Words.add("having");
			m_Words.add("he");
			m_Words.add("hello");
			m_Words.add("help");
			m_Words.add("hence");
			m_Words.add("her");
			m_Words.add("here");
			m_Words.add("hereafter");
			m_Words.add("hereby");
			m_Words.add("herein");
			m_Words.add("hereupon");
			m_Words.add("hers");
			m_Words.add("herself");
			m_Words.add("hi");
			m_Words.add("him");
			m_Words.add("himself");
			m_Words.add("his");
			m_Words.add("hither");
			m_Words.add("hopefully");
			m_Words.add("how");
			m_Words.add("howbeit");
			m_Words.add("however");
			m_Words.add("i");
			m_Words.add("ie");
			m_Words.add("if");
			m_Words.add("ignored");
			m_Words.add("immediate");
			m_Words.add("in");
			m_Words.add("inasmuch");
			m_Words.add("inc");
			m_Words.add("indeed");
			m_Words.add("indicate");
			m_Words.add("indicated");
			m_Words.add("indicates");
			m_Words.add("inner");
			m_Words.add("insofar");
			m_Words.add("instead");
			m_Words.add("into");
			m_Words.add("inward");
			m_Words.add("is");
			m_Words.add("it");
			m_Words.add("its");
			m_Words.add("itself");
			m_Words.add("j");
			m_Words.add("just");
			m_Words.add("k");
			m_Words.add("keep");
			m_Words.add("keeps");
			m_Words.add("kept");
			m_Words.add("know");
			m_Words.add("knows");
			m_Words.add("known");
			m_Words.add("l");
			m_Words.add("last");
			m_Words.add("lately");
			m_Words.add("later");
			m_Words.add("latter");
			m_Words.add("latterly");
			m_Words.add("least");
			m_Words.add("less");
			m_Words.add("lest");
			m_Words.add("let");
			m_Words.add("like");
			m_Words.add("liked");
			m_Words.add("likely");
			m_Words.add("little");
			m_Words.add("ll"); // added to avoid words like you'll,I'll etc.
			m_Words.add("look");
			m_Words.add("looking");
			m_Words.add("looks");
			m_Words.add("ltd");
			m_Words.add("m");
			m_Words.add("mainly");
			m_Words.add("many");
			m_Words.add("may");
			m_Words.add("maybe");
			m_Words.add("me");
			m_Words.add("mean");
			m_Words.add("meanwhile");
			m_Words.add("merely");
			m_Words.add("might");
			m_Words.add("more");
			m_Words.add("moreover");
			m_Words.add("most");
			m_Words.add("mostly");
			m_Words.add("much");
			m_Words.add("must");
			m_Words.add("my");
			m_Words.add("myself");
			m_Words.add("n");
			m_Words.add("name");
			m_Words.add("namely");
			m_Words.add("nd");
			m_Words.add("near");
			m_Words.add("nearly");
			m_Words.add("necessary");
			m_Words.add("need");
			m_Words.add("needs");
			m_Words.add("neither");
			m_Words.add("never");
			m_Words.add("nevertheless");
			m_Words.add("new");
			m_Words.add("next");
			m_Words.add("nine");
			m_Words.add("no");
			m_Words.add("nobody");
			m_Words.add("non");
			m_Words.add("none");
			m_Words.add("noone");
			m_Words.add("nor");
			m_Words.add("normally");
			m_Words.add("not");
			m_Words.add("nothing");
			m_Words.add("novel");
			m_Words.add("now");
			m_Words.add("nowhere");
			m_Words.add("o");
			m_Words.add("obviously");
			m_Words.add("of");
			m_Words.add("off");
			m_Words.add("often");
			m_Words.add("oh");
			m_Words.add("ok");
			m_Words.add("okay");
			m_Words.add("old");
			m_Words.add("on");
			m_Words.add("once");
			m_Words.add("one");
			m_Words.add("ones");
			m_Words.add("only");
			m_Words.add("onto");
			m_Words.add("or");
			m_Words.add("other");
			m_Words.add("others");
			m_Words.add("otherwise");
			m_Words.add("ought");
			m_Words.add("our");
			m_Words.add("ours");
			m_Words.add("ourselves");
			m_Words.add("out");
			m_Words.add("outside");
			m_Words.add("over");
			m_Words.add("overall");
			m_Words.add("own");
			m_Words.add("p");
			m_Words.add("particular");
			m_Words.add("particularly");
			m_Words.add("per");
			m_Words.add("perhaps");
			m_Words.add("placed");
			m_Words.add("please");
			m_Words.add("plus");
			m_Words.add("possible");
			m_Words.add("presumably");
			m_Words.add("probably");
			m_Words.add("provides");
			m_Words.add("q");
			m_Words.add("que");
			m_Words.add("quite");
			m_Words.add("qv");
			m_Words.add("r");
			m_Words.add("rather");
			m_Words.add("rd");
			m_Words.add("re");
			m_Words.add("really");
			m_Words.add("reasonably");
			m_Words.add("regarding");
			m_Words.add("regardless");
			m_Words.add("regards");
			m_Words.add("relatively");
			m_Words.add("respectively");
			m_Words.add("right");
			m_Words.add("s");
			m_Words.add("said");
			m_Words.add("same");
			m_Words.add("saw");
			m_Words.add("say");
			m_Words.add("saying");
			m_Words.add("says");
			m_Words.add("second");
			m_Words.add("secondly");
			m_Words.add("see");
			m_Words.add("seeing");
			m_Words.add("seem");
			m_Words.add("seemed");
			m_Words.add("seeming");
			m_Words.add("seems");
			m_Words.add("seen");
			m_Words.add("self");
			m_Words.add("selves");
			m_Words.add("sensible");
			m_Words.add("sent");
			m_Words.add("serious");
			m_Words.add("seriously");
			m_Words.add("seven");
			m_Words.add("several");
			m_Words.add("shall");
			m_Words.add("she");
			m_Words.add("should");
			m_Words.add("since");
			m_Words.add("six");
			m_Words.add("so");
			m_Words.add("some");
			m_Words.add("somebody");
			m_Words.add("somehow");
			m_Words.add("someone");
			m_Words.add("something");
			m_Words.add("sometime");
			m_Words.add("sometimes");
			m_Words.add("somewhat");
			m_Words.add("somewhere");
			m_Words.add("soon");
			m_Words.add("sorry");
			m_Words.add("specified");
			m_Words.add("specify");
			m_Words.add("specifying");
			m_Words.add("still");
			m_Words.add("sub");
			m_Words.add("such");
			m_Words.add("sup");
			m_Words.add("sure");
			m_Words.add("t");
			m_Words.add("take");
			m_Words.add("taken");
			m_Words.add("tell");
			m_Words.add("tends");
			m_Words.add("th");
			m_Words.add("than");
			m_Words.add("thank");
			m_Words.add("thanks");
			m_Words.add("thanx");
			m_Words.add("that");
			m_Words.add("thats");
			m_Words.add("their");
			m_Words.add("theirs");
			m_Words.add("them");
			m_Words.add("themselves");
			m_Words.add("then");
			m_Words.add("thence");
			m_Words.add("there");
			m_Words.add("thereafter");
			m_Words.add("thereby");
			m_Words.add("therefore");
			m_Words.add("therein");
			m_Words.add("theres");
			m_Words.add("thereupon");
			m_Words.add("these");
			m_Words.add("they");
			m_Words.add("think");
			m_Words.add("third");
			m_Words.add("this");
			m_Words.add("thorough");
			m_Words.add("thoroughly");
			m_Words.add("those");
			m_Words.add("though");
			m_Words.add("three");
			m_Words.add("through");
			m_Words.add("throughout");
			m_Words.add("thru");
			m_Words.add("thus");
			m_Words.add("to");
			m_Words.add("together");
			m_Words.add("too");
			m_Words.add("took");
			m_Words.add("toward");
			m_Words.add("towards");
			m_Words.add("tried");
			m_Words.add("tries");
			m_Words.add("truly");
			m_Words.add("try");
			m_Words.add("trying");
			m_Words.add("twice");
			m_Words.add("two");
			m_Words.add("u");
			m_Words.add("un");
			m_Words.add("under");
			m_Words.add("unfortunately");
			m_Words.add("unless");
			m_Words.add("unlikely");
			m_Words.add("until");
			m_Words.add("unto");
			m_Words.add("useful");
			m_Words.add("uses");
			m_Words.add("using");
			m_Words.add("usually");
			m_Words.add("uucp");
			m_Words.add("v");
			m_Words.add("value");
			m_Words.add("which");
			m_Words.add("while");
			m_Words.add("whither");
			m_Words.add("who");
			m_Words.add("whoever");
			m_Words.add("whole");
			m_Words.add("whom");
			m_Words.add("whose");
			m_Words.add("why");
			m_Words.add("willing");
			m_Words.add("wish");
			m_Words.add("within");
			m_Words.add("without");
			m_Words.add("wonder");
			m_Words.add("would");
			m_Words.add("would");
			m_Words.add("x");
			m_Words.add("y");
			m_Words.add("yes");
			m_Words.add("yet");
			m_Words.add("you");
			m_Words.add("your");
			m_Words.add("yours");
			m_Words.add("yourself");
			m_Words.add("yourselves");
			m_Words.add("z");
			m_Words.add("zero");
	    }   

	    public static void createInvertedList(Connection conn) throws SQLException
	    { 
		Integer attributeMapping = 1;
		Integer contador	 = 0;
		try {
			Statement st            	= conn.createStatement();
			String    sqlSt         	= "select tablename from pg_tables where schemaname!='pg_catalog' and schemaname !='information_schema'"; 
			ResultSet rs            	= st.executeQuery(sqlSt);
			String    ctid;
			// list all tables are stored in database
			while (rs.next()) {
				//System.out.println("---"+rs.getString(1)+"---");
				Statement st2  			= conn.createStatement();
				String    sqlSt2		= "select ctid,* from "+rs.getString(1);
				ResultSet rs2   		= st2.executeQuery(sqlSt2);
				ResultSetMetaData rsMetaData    = rs2.getMetaData();
				int numberOfColumns 		= rsMetaData.getColumnCount();
				// scan all tuples for specific table
				while (rs2.next()) {
				   ctid = rs2.getString(1);
				   for(int x=1;x<=numberOfColumns;x++)
				   {
					// verify if column can be indexed. ex: string, name and so on
					if(rsMetaData.getColumnType(x)==12) {
					  String tupleKeywords				= rs2.getString(x);
					  String[] tokens 				= new String[100];
					  // store relation.attribute column and mapping to integer number
					  if(attribute2map.get(rs.getString(1)+"."+rsMetaData.getColumnName(x)) == null) {
						attribute2map.put(rs.getString(1)+"."+rsMetaData.getColumnName(x),attribute2map.size()+1);
						map2attribute.put(map2attribute.size()+1,rs.getString(1)+"."+rsMetaData.getColumnName(x));
					  }
					  else 
					      attributeMapping				= attribute2map.get(rs.getString(1)+"."+rsMetaData.getColumnName(x)); 
					  distinctAttrbutes.put(attribute2map.get(rs.getString(1)+"."+rsMetaData.getColumnName(x)),new HashMap<String,Integer>());
					  if(tupleKeywords!=null) tokens		= tokenize(tupleKeywords);
					  for(String token: tokens) {
					   if(token!=null) {
					    if (!is(token)) { 
					     if(termIndex.containsKey(token))  {
						if(termIndex.get(token).get(attributeMapping) != null)
							contador = termIndex.get(token).get(attributeMapping);
						if(contador>0) termIndex.get(token).put(attributeMapping,contador+1);
						else  termIndex.get(token).put(attributeMapping,1);
						termIndexTuples.get(token).put(ctid,attributeMapping);
						distinctAttrbutes.get(attributeMapping).put(token,1);
					     }
					     else  {
					      termIndex.put(token,new HashMap<Integer,Integer>());
					      termIndex.get(token).put(attributeMapping,1);
					      termIndexTuples.put(token,new HashMap<String,Integer>());
					      termIndexTuples.get(token).put(ctid,attributeMapping);
					      distinctAttrbutes.get(attributeMapping).put(token,1);
					     }
					    // private static HashMap<String,HashMap<Integer,Integer>> termIndex    = new HashMap<String, HashMap<Integer,Integer>>();
					    } //is
					   }
					  }
					} //end if
				   }
				}
			}
		} catch (SQLException se) {
		  System.out.println("Couldn't connect: print out a stack trace and exit.");
		    se.printStackTrace();
		    System.exit(1);
		}
	    }
	    public static Graph getSchemaGraph(Connection conn) throws SQLException
	    {
		Graph G   = new Graph();
		try {
			Statement st            = conn.createStatement();
			String    sqlSt         = "SELECT ccu.table_name AS foreign_table_name,ccu.column_name AS foreign_column_name," +
			"tc.table_name, kcu.column_name FROM information_schema.table_constraints AS tc " +
			" JOIN information_schema.key_column_usage AS kcu ON tc.constraint_name = kcu.constraint_name"+
			" JOIN information_schema.constraint_column_usage AS ccu ON ccu.constraint_name = tc.constraint_name" +
			" WHERE constraint_type = 'FOREIGN KEY'";
			ResultSet rs            = st.executeQuery(sqlSt);
			while (rs.next()) { 
				//inproceeding1.inproceedingid: relationpersoninproceeding311.inproceedingid
				G.addVertex(rs.getString(1));
				G.addVertex(rs.getString(3));
				G.addEdge(rs.getString(1),rs.getString(1)+"."+rs.getString(2));
				G.addEdge(rs.getString(1)+"."+rs.getString(2),rs.getString(3)+"."+rs.getString(4));
				//G.addEdge(rs.getString(1)+"."+rs.getString(2),rs.getString(3));
				G.addEdge(rs.getString(3)+"."+rs.getString(4),rs.getString(1)+"."+rs.getString(2));
				G.addEdge(rs.getString(3), rs.getString(3)+"."+rs.getString(4));
				G.addEdge(rs.getString(1)+"."+rs.getString(2),rs.getString(1));
				G.addEdge(rs.getString(3)+"."+rs.getString(4),rs.getString(3));
				if(rGraph.get(rs.getString(1))==null) rGraph.put(rs.getString(1),new HashMap<String,String>());
				rGraph.get(rs.getString(1)).put(rs.getString(1),rs.getString(1)+"."+rs.getString(2));
			//	if(rGraph.get(rs.getString(3))==null) rGraph.put(rs.getString(3),new HashMap<String,String>());
			//	rGraph.get(rs.getString(3)).put(rs.getString(1),rs.getString(1)+"."+rs.getString(2));
			}
		} catch (SQLException se) {
		  System.out.println("Couldn't connect: print out a stack trace and exit.");
		    se.printStackTrace();
		    System.exit(1);
		}
		return G;
	    }

	    public static void evaluateCN(Connection conn,String CN) throws SQLException
            {
                try {
                        Statement st            	= conn.createStatement();
                        ResultSet rs            	= st.executeQuery(CN);
			Vector<String> columnNames 	= new Vector<String>();
			if (rs != null) {
        			ResultSetMetaData columns = rs.getMetaData();
        			int i = 0;
        			while (i < columns.getColumnCount()) {
         				i++;
          				System.out.print(columns.getColumnName(i) + "\t");
          				columnNames.add(columns.getColumnName(i));
        			}
        			System.out.print("\n");

 			       while (rs.next()) {
          			for (i = 0; i < columnNames.size(); i++) {
            				System.out.print(rs.getString(columnNames.get(i))+ "\t");

          			}
          			System.out.print("\n");
        		      }
      		     }
                } catch (SQLException se) {
                  System.out.println("Couldn't connect: print out a stack trace and exit.");
                    se.printStackTrace();
                    System.exit(1);
                }
            }

	    private static boolean isValidCN(String match) {
		String[] termSet        = match.split("="); //e.g: name.name
		List<String> query	= new ArrayList<String>(q.values());
		int contador		= 0;
		if(termSet[1]!=null)  {
		     String[] termSetPart    = termSet[1].split("_");//e.g: denzel_washington
		     if(query.size()==termSetPart.length) return true;
		}
		return false;
	    }
	    private static boolean isCoveringCN(Deque CN,ArrayList<String> keywords) {
	       ArrayList<String> keywordsStrip   = new ArrayList<String>();
	       ArrayDeque<String> aDeque = new ArrayDeque<String>(CN);
	       for (Iterator<String> i = aDeque.iterator(); i.hasNext();) {
		  keywordsStrip.add(i.next());
	       } 
	       if(keywords.equals(getKeywords(keywordsStrip))) return true;
	       else return false;

	    }
	     private static String getKeywords(String queryMatch) {
	        String keywords = new String();
          	if(queryMatch.contains("=")) {
             		String[] termSet      = queryMatch.split("=");
             		if(termSet[1]!=null)  {
               			String[] termSetPart    = termSet[1].split("_");//e.g: denzel_washington
				int contador=0;
               			for(String term: termSetPart) {
					++contador;
                			keywords+=term;
					if(contador<termSetPart.length) keywords+=",";
				}
             		}
          	}
       		return keywords;
    	    }

	    private static void printSQL(Deque CN) {
	       ArrayDeque<String> aDeque 	= new ArrayDeque<String>(CN);
	       String WHERECLAUSE 		= new String();
	       Map<String,String> relations	= new HashMap<String,String>();
	       String match 	  		= new String();
	       Integer pairCheck 		= 0;
	       String pair 			= new String();
	       String SQL			= new String();
	       //to_tsvector('english',name) @@ to_tsquery('english','denzel,washington');
	       for (Iterator<String> i = aDeque.iterator(); i.hasNext();) {
		 match = i.next();
		 if(!match.contains(".")) System.out.print(" "); // NOT MATCH WITH ANOTHER TABLE OR TERM
		 if(match.contains("="))  { 
			WHERECLAUSE+="to_tsvector('english',"+getAttribute(match)+") @@ to_tsquery('english','"+getKeywords(match)+"')"; // MATCH IS PREDICATE WITH TERM
			relations.put(getRelation(match),match);
			if(i.hasNext())
				WHERECLAUSE+=" AND ";
			
		 }
		 else {
			if(match.contains(".") && pairCheck==0) {
				pair=match; 
				++pairCheck;
				relations.put(getRelation(match),match);
			}
			else {
				if(match.contains(".") && pairCheck>0)  {
					pair+="="+match;
					++pairCheck;
					relations.put(getRelation(match),match);
				}
			}
		 }
		 if(pairCheck>1) {
			WHERECLAUSE+=" " + pair;
			pairCheck=0;
			if(i.hasNext()) 
				WHERECLAUSE+=" AND ";
		 }
	       }
	       int contador=0;
	       System.out.print("CN"+nroCNs+": SELECT * FROM ");
	       SQL="SELECT * FROM ";
	       for(String rel:relations.keySet()) {
		  System.out.print(rel);SQL+=rel;
		  ++contador;
		  if(contador<relations.size()) { 
			System.out.print(",");
			SQL+=",";
		  }
	       }
	       System.out.println(" WHERE "+WHERECLAUSE);
	       SQL+=" WHERE "+WHERECLAUSE;
	       SQLs.add(SQL);
	    }

	     private static String getRelation(String queryMatch) {
		 String match = queryMatch;
		 if(queryMatch.contains("=")) {
		     String[] termSet      = queryMatch.split("=");
		     if(termSet[0]!=null) match=termSet[0]; 
		 }
		 if(match.contains(".")) {
		     String[] termSet2      = match.split("\\.");
		     if(termSet2[0]!=null) match=termSet2[0];
          	}
       		return match;
    	    }
	    private static String getAttribute(String queryMatch) {
                 String match = queryMatch;
                 if(queryMatch.contains("=")) {
                     String[] termSet      = queryMatch.split("=");
                     if(termSet[0]!=null) match=termSet[0];
                 }
                return match;
            }


    // breadth-first search from multiple sources
    private static void modcngen(Graph matchingGraph, List<String> queryMatch,Integer TMAX,ArrayList<String> keywords) {
	String[] queryMatchArray = queryMatch.toArray(new String[queryMatch.size()]);
	String   match		 = queryMatchArray[0];
	boolean  stopPoint	 = false;
        HashMap<String,Boolean> unmarkedVertices = new HashMap<String,Boolean>();
	//create the unmarked vertices
	for(String vertice:matchingGraph.vertices())
	   unmarkedVertices.put(vertice,false);
		Deque partialCN		 = new LinkedList();
		partialCN.add(match);
		Deque Q 		 = new LinkedList();
		Q.add(partialCN);
		unmarkedVertices.put(match,true);
    		while (!Q.isEmpty()) {
		   Deque T=(Deque) Q.poll();
	    	   String v = (String)T.peek();
                   for (String w : matchingGraph.adjacentTo(v)) {
			if(!isVisitedMatch(w,T)){
				Deque T2 = new LinkedList(T);
				T2.addFirst(w);
				if((w.contains("=") && queryMatch.contains(w)) || !w.contains("=")) { 
				   if(isCoveringCN(T2,keywords)) {
					if(!isTriangle(T2)) {
						++nroCNs;
						printSQL(T2);
						Q.clear();
						stopPoint=true;
					}
				   }
				   else if(T2.size()<TMAX) Q.add(T2);
				}
			}
                   }
                }  
    }
    private static boolean isTriangle(Deque pilha) {
	int startTriangle=0;
	int ttl=0;
	boolean triangle=false;
        ArrayDeque<String> apilha = new ArrayDeque<String>(pilha);
	String triangleRelation = new String();
	String rel = new String();
	for(String node: apilha) {
	   String[] relation      = node.split("\\.");
	   if(relation.length==0) rel=node;
	   else rel=relation[0];
	   if(node.contains("=")) {
		--ttl;
		if(startTriangle==ttl && ttl>0) triangle=true; 
		startTriangle=0;
		ttl=0;
		triangleRelation=rel;
	   }
	   else if(rel.equalsIgnoreCase(triangleRelation))
	     ++startTriangle;
	   ++ttl;
	}
	return triangle;
    }
    private static boolean isMatch(String vertice) {
       if(vertice.contains("=")) return true;
       else return false;
    }
    private static boolean isVisitedMatch(String vertice,Deque pilha) {
	if(pilha.contains(vertice) && isMatch(vertice))
		return true;
	else  if(pilha.contains(vertice.substring(0, vertice.length()-1))  && isMatch(vertice)) return true;
	else
		return false;	
    }

    private static ArrayList<String> getKeywords(List<String> queryMatch) {
	ArrayList<String> keywords = new ArrayList<String>();
	for(String match:queryMatch) {
          if(match.contains("=")) {
             String[] termSet      = match.split("=");
             if(termSet[1]!=null)  {
                String[] termSetPart    = termSet[1].split("_");//e.g: denzel_washington
                for(String term: termSetPart)
		   keywords.add(term);
             }
          }
       }
       Collections.sort(keywords);
       return keywords;
    }

    public static HashMap<String,ArrayList<String>> createTermSets(List<String> query) {
	HashMap<String,ArrayList<Integer>> tupleSets  		= new HashMap<String, ArrayList<Integer>>();	
        HashMap<String,ArrayList<String>>  tupleSetsTerm 	= new HashMap<String, ArrayList<String>>();	
        List<List<String>> 		   resultLists 		= new ArrayList<List<String>>();
	int	maxKeywordJoin 					=0;
	String  maxKeyword					= new String();
	int	p;
	for(String keyword:query) {
            for(String tupleID: termIndexTuples.get(keyword).keySet()) {
		if(tupleSets.get(tupleID) == null) tupleSets.put(tupleID, new ArrayList<Integer>());
		if(tupleSetsTerm.get(tupleID) == null) tupleSetsTerm.put(tupleID, new ArrayList<String>());
                tupleSets.get(tupleID).add(termIndexTuples.get(keyword).get(tupleID));
		tupleSetsTerm.get(tupleID).add(map2attribute.get(termIndexTuples.get(keyword).get(tupleID))+"="+keyword);
	    }
        }
	HashMap<String,ArrayList<String>> termSets	= new HashMap<String,ArrayList<String>>();
        for(String tupleID: tupleSets.keySet()) {
	     if(tupleSets.get(tupleID).size()>0) {
               /*for(Integer attributeid:tupleSetsTerm.get(tupleID).keySet()) {
		    System.out.print(map2attribute.get(attributeid)+"="+tupleSetsTerm.get(tupleID).get(attributeid)+" ");
	       }*/
	       List<String> attributeKeyword 	= new ArrayList<String>();
	       String attributeBefore		= new String();
	       String attributeCurrent		= new String();
	       String keywordJoin		= new String();
	       String keywordJoinAttribute	= new String();
	       int startCounter=0;
               for(String attributeTerm:tupleSetsTerm.get(tupleID))
	       { // all these attribute+keyword are in same tuple
		  //termSets.put(attributeTerm,0);
		  p	= attributeTerm.indexOf('=');
		  if(p>=0) {
		    attributeCurrent = attributeTerm.substring(0,p);
	          }	
		  if(termSets.get(attributeTerm.substring(p+1,attributeTerm.length())) ==null) termSets.put(attributeTerm.substring(p+1,attributeTerm.length()), new ArrayList<String>());
		  if(!(termSets.get(attributeTerm.substring(p+1,attributeTerm.length())).contains(attributeTerm))) termSets.get(attributeTerm.substring(p+1,attributeTerm.length())).add(attributeTerm);
		  if(startCounter==0) attributeBefore=attributeCurrent;
	          if(attributeCurrent.equals(attributeBefore)) {
			if(startCounter==0) {
				keywordJoinAttribute+=attributeCurrent+"="+attributeTerm.substring(p+1,attributeTerm.length()) ;
				keywordJoin+=attributeTerm.substring(p+1,attributeTerm.length());
			}
		        else {
				keywordJoinAttribute+="_"+attributeTerm.substring(p+1,attributeTerm.length());
				keywordJoin+="_"+attributeTerm.substring(p+1,attributeTerm.length());
			} 
			++startCounter;
		  }
		  attributeBefore	= attributeCurrent;
	       }
	       if(startCounter>maxKeywordJoin) {
		maxKeywordJoin=startCounter;
		maxKeyword=keywordJoin;
	       }
	       if(termSets.get(keywordJoin)==null) termSets.put(keywordJoin,new ArrayList<String>());
	       if(!(termSets.get(keywordJoin).contains(keywordJoinAttribute)))    termSets.get(keywordJoin).add(keywordJoinAttribute);
             }
	}
	// add empty sets
	int currentSize;
        for(String term:termSets.keySet()) {
	   currentSize = termSets.get(term).size();
	  // if(term!=maxKeyword && maxKeywordJoin==query.size()) termSets.get(term).add("{}");
	    termSets.get(term).add("{}");
	} 
	// create the cartesian product
	List<List<String>>  matrix          = new ArrayList<List<String>>();
	for(String term:termSets.keySet()) {
		//change to PowerSet
	 	//HashMap<String,ArrayList<String>> singletupleSets      = new HashMap<String,ArrayList<String>>();
		//for(String tupleset:termSets.get(term)) {
		  // singletupleSets.put(term,new ArrayList<String>());
		  // singletupleSets.get(term).add(tupleset);
		   matrix.add(new ArrayList(termSets.get(term)));
		 //  matrix.add(new ArrayList(singletupleSets.get(term)));
		   System.out.println("termset:"+termSets.get(term));
	//	}
	}
	System.out.println("adding distributions...");
	distributions.addAll(genDistributions(matrix,query));

	return termSets;
    }
 
    public static List<List<String>> generate(ArrayList<List<String>> sets,List<String> Q) {
      int solutions = 1;
      boolean validCovering = true;
      List<List<String>> matches = new ArrayList<List<String>>();
      for(int i = 0; i < sets.size(); solutions *= sets.get(i).size(), i++);
      for(int i = 0; i < solutions; i++) {
        int j = 1;
	List<String>       match   = new ArrayList<String>();
	validCovering		   = true;
	List<String>       terms   = new ArrayList<String>();
        for(List<String> set : sets) {
	    //test if match is total
	    String predicate          = set.get((i/j)%set.size());
            if(predicate!="{}") {
              String[] termSet        = predicate.split("="); //e.g: name.name
              if(termSet[1]!=null)  {
		 String[] termSetPart    = termSet[1].split("_");//e.g: denzel_washington
                 String[] tablePart      = termSet[0].split("\\.");//e.g: name
                 for(String termPart: termSetPart) {
                  if(terms.contains(termPart)) validCovering=false;
                  else terms.add(termPart);
                 }
	      }
            }
	    match.add(set.get((i/j)%set.size()));
            //System.out.print(set.get((i/j)%set.size()) + " ");
            j *= set.size();
        }
	//check if match is minimal
        for(String keyword:Q) {
           if(!terms.contains(keyword)) validCovering=false;
        }
	if(validCovering) matches.add(match);
        //System.out.println();
      }
      return matches;
    }

    public static List<List<String>> genDistributions(List<List<String>> lists,List<String> Q) {
      List<List<String>> resultLists = new ArrayList<List<String>>();
      List<String>    terms          = new ArrayList<String>();
      boolean validCovering=true;
      if (lists.size() == 0) {
        resultLists.add(new ArrayList<String>());
        return resultLists;
      } else {
        List<String> firstList = lists.get(0);
        List<List<String>> remainingLists = genDistributions(lists.subList(1, lists.size()),Q);
        validCovering=true;
        for (String condition : firstList) {
          //System.out.println("condition:"+condition+" firstList:"+firstList);
          for (List<String> remainingList : remainingLists) {
            terms.clear();
            validCovering=true;
            if(condition!="{}") {
              String[] termSet        = condition.split("="); //e.g: name.name
	      if(termSet[1]!=null)  {
                 String[] termSetPart    = termSet[1].split("_");//e.g: denzel_washington
                 String[] tablePart      = termSet[0].split("\\.");//e.g: name
                 for(String termPart: termSetPart) {
                  if(terms.contains(termPart)) validCovering=false;
                  else terms.add(termPart);
                 }
              }
            }
            for(String condition2 : remainingList) {
               if(condition2!="{}") {
                String[] termSet2        = condition2.split("="); //e.g: name.name
                String[] termSetPart2    = termSet2[1].split("_");//e.g: denzel_washington
                String[] tablePart2      = termSet2[0].split("\\.");//e.g: name
                for(String termPart2: termSetPart2) {
                   if(terms.contains(termPart2)) validCovering=false;
                   else terms.add(termPart2);
                }
              }
            }
            //System.out.println("remainList:"+remainingList+" RLists:"+remainingLists);
            ArrayList<String> resultList = new ArrayList<String>();
            if(validCovering) {
                resultList.add(condition);
                resultList.addAll(remainingList);
                resultLists.add(resultList);
            }
          }
        }
      }
      return resultLists;
    }


/*
    public SortedMap<Double,String[]> CNRank(List<List<String>>  queryMatches,String keywords)
    {
        SortedMap<Double, String[]> CNRank                              = new TreeMap<Double, String[]>();
        HashMap<String,List<String>> occurrences 	                = new HashMap<String,List<String>>();
        HashMap<String,Integer> CNsametuple                             = new HashMap<String,Integer>();
        double nk;   // is the total number of occurrences of the term k in database
        double fki; // the number of occurrences of term k in the attribute Ai
        double wik; // peso do termo
        double accwik =0;
        /*
         * fki/fmax * log(1 + fki) / log(1+nk)
         *
         * AF MODEL
         * where fki is the number of occurrences of term k in the values of the attribute Ai, fmax is the frequency of the
                   term that has more occurrences in the values of the attribute Ai, and nk is the total number of occurrences of
                   the term k in database.

         */

        /*
         * CNs is
         * [relationName.Attribute:Term],[...]
         * [...]
         */
  /*      for (String[] CN: CNs)
        {
                wik                      = 0.0;
                int contador = 0;
                for (String CNpart:CN) //[relationName.Attribute:Term],[...]
                {
                        /*
                         * CN is [relationName.Attribute:Term]
                         */

    /*                    nk=fki=0.0;
                        List<String>  tuples                       = new ArrayList<String>();
                        String[] relationAttributeTerm             = CNpart.split(":");
                        occurrences                                = this.invertedIndex.get(relationAttributeTerm[1]);
                        if(occurrences!=null)
                        {
                                nk                                 = this.frequencyTerm.get(relationAttributeTerm[1]);
                                tuples                             = occurrences.get(relationAttributeTerm[0]);
                                fki                                = (double) tuples.size();
                                //LOG(1+'fki')/ LOG(1+'nk')

                                if(contador==0) wik                = Math.log(1+fki) / Math.log(1+nk);
                                else                wik           *= Math.log(1+fki) / Math.log(1+nk);
                                ++contador;
                        }
                        //System.out.println("rel+att+term:"+relationAttributeTerm[0]+":"+relationAttributeTerm[1]+" fki:"+fki+" wik:"+wik);
                }
                if(occurrences!=null)           accwik += wik;
                CNRank.put(wik, CN);
                //CNRank.put(CNs[index], wik);
        }

        Double[] pesos          =new Double[CNRank.size()];
        CNRank.keySet().toArray(pesos);

        Arrays.sort(pesos, Collections.reverseOrder());   // This is what you mean.

        for (Double peso: pesos) {
                String[] CNdata = CNRank.get(peso);
                for(String CNvalue:CNdata)
               System.out.print("["+CNvalue+"]");
                System.out.println("("+peso+")");
        }

        CNsametuple     = getCNsameTuple(keywords);
        // checking same tuples for keywords involved in the query
        for(String CN : CNsametuple.keySet())
        {
                String[] terms;
                terms           = tokenize(keywords);
                System.out.print("CN premium:");
                for(int x=0;x<terms.length;x++)
                        System.out.print("["+CN+":"+terms[x]+"] ");
                System.out.println(" ");
        }

        return CNRank;
    }
*/
    public static void main(String[] args) {
       List <String> Q	 		  		= new ArrayList<String>();      // query set
       List<List<String>>           queryMatches  	= new ArrayList<List<String>>();
       String  server, database, username, password;
       Properties prop        			  	= new Properties();
       HashMap<String,ArrayList<String>> termsSet 	= new HashMap<String,ArrayList<String>>();	
       SortedMap<Double,List<String>> QMRanked   	= new TreeMap<Double, List<String>>();
       Connection conn                  	  	= null;
       startStopwords();
       long timeTI1=System.currentTimeMillis() ;
       try {
         //Load a properties file for dbretriever
         prop.load(new FileInputStream("config.properties"));
         //get the property value and print it out
         database             = prop.getProperty("database");
         username             = prop.getProperty("username");
         password             = prop.getProperty("password");
         server               = prop.getProperty("server");
         conn                 = getDBConnection("org.postgresql.Driver","postgresql",server,"5432",database,username,password);
       } catch (IOException ex) {
            	 ex.printStackTrace();
       }

       try {
         System.out.println("Creating Term Index in Memory...");
         createInvertedList(conn);
         long timeTI2=System.currentTimeMillis() ;
         System.out.println("Time to index.......="+(timeTI2-timeTI1)+"\n");
         System.out.println("TermIndex size="+termIndex.size());
       } catch (SQLException se) {
          System.out.println("Couldn't connect: print out a stack trace and exit.");
            se.printStackTrace();
            System.exit(1);
       }
       while(true) {
         try{
            InputStreamReader isr 	= new InputStreamReader( System.in );  
            BufferedReader stdin 	= new BufferedReader( isr );  
            System.out.print("Input Keywords:");
            String input 		= stdin.readLine();
            Q.clear();
	    q.clear();
	    timeTI1=System.currentTimeMillis() ;
            terminalSets.clear();
            String[] 		tokens	= new String[100];
            if(input!=null) 
        	tokens  		= tokenize(input);
            int x=1;
            for(String token: tokens) {
               if(termIndexTuples.get(token) !=null) {
		Q.add(token);
                q.put(x,token);
                ++x;
	       }
           }
         } catch (IOException ex) {
                 ex.printStackTrace();
         }
         long time5=System.currentTimeMillis() ;
         for(int i=0;i<Q.size(); ++i) {
           s.put(i,1);
           m.put(i,1);
         }
	 distributions.clear();
	 termsSet.clear();
	 termsSet	= createTermSets(Q);
	 System.out.println("finish termsets created");
	 Integer fkj	= 0;
	 double  wjk	= 0.0; // weights according to the TF-IAF model
	 double  accwjk = 0.0;
	 double  scorei = 0.0;
	 Integer Nj	= 0; // total number of distinct terms that occur in values of the attribute Bj
	 Integer Na     = 0; // total number of attributes in the database
	 Integer Ck     = 0; // number of attributes in whose values the term k occur
	 // check covering conditions for each match query
	 QMRanked.clear();
	 int nroMatches=0;
         for(List<String> match: distributions)
         {
            List<String> matchValid     = new ArrayList<String>();
	    List<String> terms 		= new ArrayList<String>();
	    HashMap<String,Integer> RLR = new HashMap<String,Integer>();// conditon R->L->R
	    Integer sizeMatch           = 0;
	    boolean validCovering	= true;
	    accwjk			= 0.0;
	    scorei			= 0.0;
	    ++nroMatches;
	    RLR.clear();
	    for(String matchPart: match)
	    {   wjk =0.0;
	
		if(matchPart!="{}") {
			++sizeMatch;
		        matchValid.add(matchPart); //something like that name.name=denzel
	        	String[] termSet   	= matchPart.split("="); //e.g: name.name
			String[] termSetPart    = termSet[1].split("_");//e.g: denzel_washington
			String[] tablePart      = termSet[0].split("\\.");//e.g: name
			if(tablePart.length>1) {
 				if(RLR.get(tablePart[0])==null) RLR.put(tablePart[0],1);
				else RLR.put(tablePart[0],RLR.get(tablePart[0])+1);
			}
			int contSameTuple       = 0; 
			for(String termPart: termSetPart) {
			        fkj	=0;
				Nj      =0;
				Na      =0;
				terms.add(termPart);
                                if(termIndex.get(termPart).get(attribute2map.get(termSet[0]))!=null) {
				  fkj = termIndex.get(termSetPart[0]).get(attribute2map.get(termSet[0])); //frequency fkj of term tk in values of attribute Bj
				  Nj  = termIndex.size();
				  Na  = attribute2map.size();
				  Ck  = termIndex.get(termPart).size();  
				  if(Nj>0) wjk += (Math.log(1+fkj)/Math.log(1+Nj)) * Math.log(1+Na/Ck);
				  else wjk=0.0;
				  contSameTuple +=2;
				}
			}
		        if(accwjk==0.0) accwjk = wjk;
			else accwjk *= wjk;
		}
		scorei = accwjk * 1/match.size();
		//scorei=nroMatches; // just before paper vldb
	    }	
	    // check RLR condition
	    for(Integer RLRcount:RLR.values())
	    {
		if(RLRcount==sizeMatch && RLRcount>1) validCovering=false;
	    } 
	    //check if match is total 
	    for(String keyword:Q) {
	 	if(!terms.contains(keyword)) validCovering=false;
	    } 
	    if(validCovering) QMRanked.put(scorei,matchValid);
	    terms.clear();
	  }
	 /// ================ CNRANK part ================================================
          Double[] pesos  = new Double[QMRanked.size()];
	  int topk =0;
          QMRanked.keySet().toArray(pesos);
          Arrays.sort(pesos, Collections.reverseOrder());   // This is what you mean.
	  distributions.clear();
	  int totalQM=0;
	  System.out.println("========== QUERY MATCHES RANKED ==========");
          for (Double peso: pesos) {
	    ++totalQM;
            if(topk<10000) {
		System.out.println(QMRanked.get(peso)+"("+peso+")");
		distributions.add(QMRanked.get(peso));
	    }
	    ++topk;
          } 
	  //========================end CNRank part ----------------------------
	  System.out.println("Total QM="+totalQM);
         //print all partial valid CNs
	 System.out.println("====== CANDIDATE NETWORKS  ================");
         nroCNs = 0;
	 SQLs.clear();
         for(List<String> queryMatch: distributions)
         {
            Graph G   = new Graph();  // need to be improve it, avoid to get all time the schemagraph
            try {  
               G         = getSchemaGraph(conn);
            } catch (SQLException se) {
            System.out.println("Couldn't connect: print out a stack trace and exit.");
               se.printStackTrace();
               System.exit(1);
            }
            for(String predicate: queryMatch) {
  	      StringTokenizer st                = new StringTokenizer(predicate,"=");
              String terminalEdge               = st.nextElement().toString();  //employee.name
              StringTokenizer st2               = new StringTokenizer(terminalEdge,"."); //employee
              String terminalEdgeRoot   	= st2.nextElement().toString();
              G.addVertex(predicate);
	      if(rGraph.get(terminalEdgeRoot)!=null) {
	        for(String adj:rGraph.get(terminalEdgeRoot).values()) {
		   G.addEdge(predicate,adj);
		   G.addEdge(adj,predicate);
                }
	      }
           }
	   modcngen(G,queryMatch,12,getKeywords(queryMatch));
         } 
	 System.out.println("Total de CNs:"+nroCNs);
         long timeTIF=System.currentTimeMillis() ;
         System.out.println("Time to generate CNs="+(timeTIF-timeTI1)+"\n");
	 System.out.println("================= EVALUATE CN =============");
         try {
		evaluateCN(conn,SQLs.get(0));
	 } catch (SQLException se) {
            System.out.println("Couldn't connect: print out a stack trace and exit.");
               se.printStackTrace();
               System.exit(1);
         }

      }
   }
}

