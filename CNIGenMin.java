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
import java.util.Map;

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

public class CNIGenMin {

    private static ArrayList<ArrayList<String>> terminalSets 			= new ArrayList<ArrayList<String>>();
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
    private static HashMap<String,Integer> attribute2map		        = new HashMap<String,Integer>(); 
    private static ArrayList<String> eclatResult				= new ArrayList<String>();
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
        			  if(tupleKeywords!=null) tokens		= tokenize(tupleKeywords);
                                  for(String token: tokens) {
                                   if(token!=null) {
                                    if (!is(token)) { 
        			     if(termIndex.containsKey(token))  {
            				termIndex.get(token).put(attributeMapping,1);
                                        termIndexTuples.get(token).put(ctid,attributeMapping);
                                     }
                                     else  {
                                      termIndex.put(token,new HashMap<Integer,Integer>());
                                      termIndex.get(token).put(attributeMapping,1);
                                      termIndexTuples.put(token,new HashMap<String,Integer>());
                                      termIndexTuples.get(token).put(ctid,attributeMapping);
                                     }
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
           		G.addEdge(rs.getString(1)+"."+rs.getString(2),rs.getString(3)+"."+rs.getString(4));
                        G.addEdge(rs.getString(3)+"."+rs.getString(4),rs.getString(1)+"."+rs.getString(2));
                        G.addEdge(rs.getString(1), rs.getString(1)+"."+rs.getString(2));
			G.addEdge(rs.getString(3), rs.getString(3)+"."+rs.getString(4));
                }
        } catch (SQLException se) {
          System.out.println("Couldn't connect: print out a stack trace and exit.");
            se.printStackTrace();
            System.exit(1);
        }
        return G;
    }

    private static void mst(Graph G, List<String> terminalSets) {
        boolean noSteiner = true;
        steinerTrees.clear();
        for(int z=1;z<terminalSets.size();++z) 
        {
        //   System.out.println("terminalset:"+terminalSets.get(z-1)+"->"+terminalSets.get(z));
        //   System.out.println("start enumerate:"+ terminalSets.get(z-1)+" to "+terminalSets.get(z));
           enumerate(G, terminalSets.get(z-1), terminalSets.get(z));
           String stm = new String();
           for(int x=0;x<steinerTrees.size();++x)
           {
          //   System.out.print("["+terminalSets.get(z-1)+" to "+terminalSets.get(z)+"]-->");
             System.out.print("MinST:");
             for(int y=0;y<steinerTrees.get(x).size();++y)
             {
	        System.out.print(steinerTrees.get(x).get(y)+" ");
             }
             System.out.println();
           }
           if(steinerTrees!=null) noSteiner = false;
           steinerTrees.clear();
        }
        if(noSteiner) 
          for(String bloco:terminalSets) System.out.print(bloco+" ");
    }
   
    // use DFS
    private static void enumerate(Graph G, String v, String t) {
        // add node v to current path from s
        path.push(v);
        onPath.add(v);
        //System.out.print(v+"->"+t);
        // found path from s to t - currently prints in reverse order because of stack
        if (v.equals(t))
        {
          Iterator itr = path.iterator();
          ArrayList<String> steinerTree = new ArrayList<String>();
          //System.out.print(v+"->"+t);
          while(itr.hasNext()) {
             Object element = itr.next();
             steinerTree.add((String) element);
          }
          if(steinerTrees.size()<1) steinerTrees.add(steinerTree);
        }
        // consider all neighbors that would continue path with repeating a node
        else {
            int tmax=0;
            for (String w : G.adjacentTo(v)) {
                if (!onPath.contains(w)) 
                   enumerate(G, w, t);
                ++tmax;
                if (tmax>2) break;
            }
        }
        // done exploring from v, so remove from path
        path.pop();
        onPath.delete(v);
    }


    public static void distributions() {
       Integer nroKeywords;
       for(ArrayList<String> partition : terminalSets)
       {
        List<List<String>>  matrix          = new ArrayList<List<String>>();
        boolean             validCN         = true;
        for(String block : partition)
        {
           if(validCN) {
            HashMap<Integer,Integer> occurrencesTuple 	= new HashMap<Integer,Integer>();
            HashMap<String,Integer> blockTriple  = new HashMap<String,Integer>();
            if(termIndex.get(block)!=null)
            {
              validCN = true;
              for(Integer attribute: termIndex.get(block).keySet()) 
                 blockTriple.put(map2attribute.get(attribute)+"="+block, 1);
            } else 
            {
              String tokens 	 = new String();
              StringTokenizer st = new StringTokenizer(block, "-");
              while (st.hasMoreElements()) {
                 String token = st.nextElement().toString();
                 tokens+=" "+token;
              }
              occurrencesTuple	= eclatBlock(tokens.toString());
              if(occurrencesTuple!=null) {
                validCN = true;
                for(Integer attribute: occurrencesTuple.keySet()) 
                  if(map2attribute.get(attribute)!=null) blockTriple.put(map2attribute.get(attribute)+"="+block,1);
              }
              else validCN = false;
            }
            if (!validCN) System.out.println("NO CANDIDATE CREATED, BECAUSE NO OCCURRENCE DETECTED");
            if (validCN) matrix.add(new ArrayList(blockTriple.keySet()));
           }
        } //block
        if(validCN) distributions.addAll(genDistributions(matrix));  // Gen valid distributions
      }
    }
 
    public static HashMap<Integer,Integer> eclatBlock(String keywords)
    {
       String[] terms;
       terms                                            = tokenize(keywords);
       Set<String> firstList				= new HashSet(0);
       if(termIndexTuples.get(terms[1])!=null) firstList = termIndexTuples.get(terms[1]).keySet();
       HashMap<Integer,Integer>  occurrencesInLine    	= new HashMap<Integer,Integer>();
       int contador = 0;
       for(String keyword:terms) {
          if(contador>1) {
             if(keyword!=null && termIndexTuples.get(keyword)!=null) firstList.retainAll(new HashSet(termIndexTuples.get(keyword).keySet()));
          }
          ++contador;
       }
       if(firstList!=null) {
        for(String term: terms) {
           for(String tupleid: firstList)  
              if(term!=null && termIndexTuples.get(term)!=null) occurrencesInLine.put(termIndexTuples.get(term).get(tupleid),1);
        }
       }
       return occurrencesInLine;
    }


    public static List<List<String>> genDistributions(List<List<String>> lists) {
      List<List<String>> resultLists = new ArrayList<List<String>>();
      if (lists.size() == 0) {
        resultLists.add(new ArrayList<String>());
        return resultLists;
      } else {
        List<String> firstList = lists.get(0);
        List<List<String>> remainingLists = genDistributions(lists.subList(1, lists.size()));
        for (String condition : firstList) {
          for (List<String> remainingList : remainingLists) {
            ArrayList<String> resultList = new ArrayList<String>();
            resultList.add(condition);
            resultList.addAll(remainingList);
            resultLists.add(resultList);
          }
        }
      }
      return resultLists;
    }

    private static String block(String prefix, ArrayList <String> terms) {
       String block = new String();
       for(int i=0;i<terms.size();i++)
       {
         if(!prefix.contains(terms.get(i)))
            block+=terms.get(i)+" ";
       }
       return block;
    }  
 
    public static boolean intersectionSet(String keywords)
    {
        HashMap<String,Integer>     tupleStack                  = new HashMap<String,Integer>();
        String[] terms;
        terms                                                   = tokenize(keywords);
        boolean sameTuple                                       =       false;
        // getting tuples for each keyw
        for(int x=0;x<terms.length;x++) {
                HashMap<String,Integer>          tuplesTouched      = new HashMap<String,Integer>();
                //put tupleid in a hashmap to count frequency
                if(termIndexTuples.get(terms[x])!=null) {
                   for(String tupleid: termIndexTuples.get(terms[x]).keySet())
                   {
                        if(tupleStack.get(tupleid)!=null && tuplesTouched.get(tupleid)==null)
                        {
                                tupleStack.put(tupleid, tupleStack.get(tupleid)+1);
                                tuplesTouched.put(tupleid, 2);
                                if(tupleStack.get(tupleid)>=terms.length-1)
                                   sameTuple=true;
                        }
                        else
                        {
                                tupleStack.put(tupleid,1);
                                tuplesTouched.put(tupleid, 1);
                        }
                   }
                }
        }
        return sameTuple;
    }
 
    private static void eclat(ArrayList<String> queryOriginal,ArrayList<String> queryNew,Integer sizeQuery) {
      ArrayList<String> temp = new ArrayList<String>();
      Integer countBlocks    = 1;
      for(int x=0;x<queryOriginal.size();++x) {
        for(int z=x+1;z<queryNew.size();z++)  {
          String tokens      = new String();
          if(!(queryNew.get(z).contains(queryOriginal.get(x)))) {
             String block 	     = queryOriginal.get(x)+"-"+queryNew.get(z);
             StringTokenizer st = new StringTokenizer(block, "-");
             countBlocks	     = st.countTokens();
             while (st.hasMoreElements()) {
                 String token = st.nextElement().toString();
                 tokens+=" "+token;
             }
             if(intersectionSet(tokens)) {
               temp.add(block);
               eclatResult.add(block);
             }
         }
        }
      }
      if(temp.size()>1) {
         System.out.println(temp+" size:"+countBlocks);
         if(countBlocks<sizeQuery) {
          System.out.println("new round for eclat");
          eclat(queryOriginal,temp,sizeQuery);
         }
      }
    }
    
    private static void partition(Integer partitionSize) {
       Integer part_num = 1;
       ArrayList<String> blocks4Partition = new ArrayList<String>();
       boolean continueBlock = true;
       for(int i=0; i<partitionSize; ++i)
          if(s.get(i) > part_num)
             part_num = s.get(i);
       for(int p = part_num;p>=1; --p) {
          StringBuilder block = new StringBuilder();
          //System.out.print("{");
          continueBlock = false;
          for(int z=0;z<partitionSize;++z) {
            if(s.get(z) == p)  {
             if(continueBlock) block.append("-");
             block.append(q.get(z+1));
             continueBlock=true;
            }
          }
          blocks4Partition.add(block.toString());
          //System.out.print("} ");
             
       }
       terminalSets.add(blocks4Partition);
    }

    private static boolean nextPartition(Integer partitionSize) {
       int i =0;
       s.put(i,s.get(i)+1);
       while((i< partitionSize-1) & (s.get(i) > m.get(i)+1)) {
         s.put(i,1);
         ++i;
         s.put(i,s.get(i)+1);
       }
       if(i== partitionSize-1)
            return false;
       int max = s.get(i);
       for(i=i-1;i>=0; --i)
          m.put(i,max);
       return true;
    }

    public static void main(String[] args) {
       ArrayList <String> Q	 		= new ArrayList<String>();      // query set
       String  server, database, username, password;
       Properties prop        			= new Properties();
       Connection conn                  	= null;
       startStopwords();
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
            terminalSets.clear();
            String[] 		tokens	= new String[100];
            if(input!=null) 
        	tokens  		= tokenize(input);
            int x=1;
            for(String token: tokens) { 
              Q.add(token);
              q.put(x,token);
              ++x;
            }
         } catch (IOException ex) {
                 ex.printStackTrace();
         }
         long time5=System.currentTimeMillis() ;
         //eclat algorithm
        // eclat.clear();
        // eclat(Q,Q,Q.size());
        // System.out.println("eclat result:"+eclatResult);
         for(int i=0;i<Q.size(); ++i) {
           s.put(i,1);
           m.put(i,1);
         }
         partition(Q.size());
         while(nextPartition(Q.size()))
           partition(Q.size());
        // System.out.println("Generating partions for query:["+Q+"]");
         System.out.println("Generating distributions based on partitions:");
         for(ArrayList<String> terminalNode: terminalSets)
             System.out.println(terminalNode);
         System.out.println("Nro de Particoes:"+terminalSets.size());
         distributions.clear();
         distributions();  // Gen valid distributions based on partitions 
         System.out.println("Nro de Distribuicoes:"+distributions.size());
         //print all partial valid CNs
         //System.out.println("-- GEN DISTRIBUTIONS ---");
         int contador = 1;
         for(List<String> partialCN: distributions)
         {
           
      //      System.out.println("==============================================");
       //     System.out.println("Terminal Sets:"+partialCN);
            Graph G   = new Graph();  // need to be improve it, avoid to get all time the schemagraph
            try {  
               G         = getSchemaGraph(conn);
            } catch (SQLException se) {
            System.out.println("Couldn't connect: print out a stack trace and exit.");
               se.printStackTrace();
               System.exit(1);
            }
            for(String terminalSet: partialCN) {

              StringTokenizer st 		= new StringTokenizer(terminalSet,"=");
              String terminalEdge		= st.nextElement().toString();  //employee.name
              StringTokenizer st2		= new StringTokenizer(terminalEdge,"."); //employee
              String terminalEdgeRoot 	= st2.nextElement().toString();
              G.addEdge(terminalSet,terminalEdgeRoot);  //author.name=michelle --> author
              G.addVertex(terminalSet);
           }
//           System.out.println("G:"+G);
           System.out.println("CN partial"+contador+":"+partialCN);mst(G,partialCN);
           ++contador;
           System.out.println();
         } 
         long time6=System.currentTimeMillis() ;
         System.out.println("Time to create CNs.......="+(time6-time5)+"\n");
      }
   }
}


