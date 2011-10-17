package de.uniluebeck.itm.wsn.devicedrivers.sunspot;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class ant_project {
    private     File buildFile;
    private     Project p;
    private     DefaultLogger consoleLogger;
    private     ByteArrayOutputStream out,error;
    private     PrintStream ps_out,ps_error;
    private     ProjectHelper helper;
    private     String out_msg ;
    private static final Logger log = LoggerFactory.getLogger(ant_project.class);

    // Public Methods -----------------------------------------------------------
  
        
        
     /**
     * Constructor
     *
     * @param path  Path of the Sunspot buil.xml  C:\\Sun\\SunSPOT\\sdk\\build.xml
     */
     public ant_project(String bpath) {
                buildFile = new File(bpath);
                p = new Project();
                p.setUserProperty("ant.file", buildFile.getAbsolutePath());
                consoleLogger = new DefaultLogger();
                out = new ByteArrayOutputStream();
                ps_out = new PrintStream(out);
                error = new ByteArrayOutputStream();
                ps_error = new PrintStream(error);
                consoleLogger.setOutputPrintStream(ps_out);
                consoleLogger.setErrorPrintStream(ps_error);
                consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
                p.addBuildListener(consoleLogger);
     }


 
// Private Methods -----------------------------------------------------------

/**
* Builds a target
*
* @param target  Target of ant build script
* @return        Output string
* @exception     Build Exception
*/
final public  String call_target(String target)  {
           this.ant_project_reset();
           try {
              p.fireBuildStarted();
              p.init();
              helper = ProjectHelper.getProjectHelper();
              p.addReference("ant.projectHelper", helper);
              helper.parse(p, buildFile);
            p.setUserProperty("port","ttyACM0" );                                                                                 //" -Dport=ttyACM0"
              p.executeTarget(target);
              p.fireBuildFinished(null);
            } catch (BuildException e) {
                 this.sendmsg(target + ">>" + out.toString());

                 p.fireBuildFinished(e);
                 throw e;
            }
             out_msg=out.toString();
             this.sendmsg(target + ">>" + out.toString());
             return out_msg;
            }

/**
* Builds a target and setting a propery
*
* @param target     Target of ant build script
* @param property   Property to set
* @param value      Value of property
* @return           Output string
* @exception     Build Exception
*/
final private   String call_target(String target,String property, String value) throws BuildException {
           this.ant_project_reset();
           try {
              p.fireBuildStarted();
              p.init();
              helper = ProjectHelper.getProjectHelper();
              p.addReference("ant.projectHelper", helper);
              helper.parse(p, buildFile);
              p.setUserProperty(property,value.substring(0,value.length()));
              p.setUserProperty("port","/dev/ttyACM0" );
              p.executeTarget(target);
              p.fireBuildFinished(null);
            } catch (BuildException e) {
              p.fireBuildFinished(e);
              this.sendmsg(target+" "+property+" " +value+">>"+out.toString()+e.toString());
              throw e;
            }
            out_msg=out.toString();
            this.sendmsg( target+" "+property+" " +value+">>"+out.toString());
            return out_msg;
}

/**
* Get route info from input node
*
* @param node   input node
* @return       Output string with one hop neighbors seperated by comma
*
* @exception     Build Exception
*/
//final  public     String   get_routes(String node) throws BuildException {
//            this.ant_project_reset();
//            try {
//              p.fireBuildStarted();
//              p.init();
//              helper = ProjectHelper.getProjectHelper();
//              p.addReference("ant.projectHelper", helper);
//              helper.parse(p, buildFile);
//              p.setUserProperty("remoteId",node );
//              p.setUserProperty("command","routes");
//              p.executeTarget("netinfo");
//              p.fireBuildFinished(null);
//            } catch (BuildException e) {
//              p.fireBuildFinished(e);
//               this.sendmsg( "netinfo"+" "+"remoteId"+" " +node+">>"+out.toString()+ e.toString() );
//              throw e;
//            }
//            this.sendmsg("netinfo"+" "+"remoteId"+" " +node+">>"+out.toString() );
//
//            out_msg="";
//            out_msg=out.toString();
//            int l=out_msg.indexOf("Route Table:");
//            int r=out_msg.lastIndexOf("[java]");
//            out_msg=out_msg.substring(l,r);
//            List node_names =   new LinkedList();
//            String[] lines=out_msg.split("\n");
//           if (lines.length<2) return node+":";
//               int line=2;
//               while (line<lines.length){
//                   String [] tok=lines[line].split("\t");
//                   if (tok.length==3 && tok[2].trim().equals("1")){
//                        node_names.add(tok[0].replace("[java]","").trim());
//                    }
//                    line++;
//               }
//         //  String output=node+":";
//            String output="";
//            for (int i=0;i<node_names.size();i++)
//                 output+= ","+node_names.get(i).toString();
//            return output;
//}

/**
* Gets the nodes listening(hello)
*
* @return        Array of Node Names
* @exception     Build Exception
*/
final public  String[] hello()  {
        String msg;
        try {
             msg = call_target("hello");
         } catch (BuildException e) {
             p.fireBuildFinished(e);
             throw e;
        }
        int l=msg.indexOf("[java] SPOT Client starting...");
        if (l<0) return null;
        int r=msg.lastIndexOf("[java]");
        msg=msg.substring(l,r);
        List node_names = new LinkedList();
        String[] lines = msg.split("\n");
        int line = 2;
        while (line < lines.length) {
            if (lines[line].split("[.]").length == 4 && lines[line].contains("[BasestationManager]")==false&& lines[line].contains("IP")==false ) {
                node_names.add(lines[line].substring(12));
            }
            line++;
        }
        String[] output = new String[node_names.size()];
        for (int i = 0; i < node_names.size(); i++) {
            output[i] = node_names.get(i).toString();
        }
        return output;
}


/**
* Check if input nodes are alive, checking if they are in hello() list
*
* @param nodes   Input nodes as string array
* @return        Output boolean table, true/false for each node
* @exception     Build Exception
*/
//final public boolean[]   areNodesAlive(String[] nodelist)   {
//        String msg = call_target("hello");
//        Map nds = new  HashMap();
//        List node_names = new LinkedList();
//        String[] lines = msg.split("\n");
//
//        int line = 2;
//        while (line < lines.length) {
//            if (lines[line].split("[.]").length == 4) {
//                node_names.add(lines[line].substring(12));
//                nds.put(lines[line].substring(12,lines[line].length()-1),true);
//            }
//            line++;
//        }
//       boolean[] output = new boolean[nodelist.length];
//        for (int i = 0; i < nodelist.length; i++) {
//            if (nds.containsKey(nodelist[i].trim() )) output[i]=true;
//            else output[i]=false;
//
//        }
//        return output;
//    }

/**
* Reset Input Nodes
*
* @param nodelist   Input nodes as string array
* @return        Output boolean table, true/false for each node
* @exception     Build Exception
*/
// final public boolean[]   resetNodes(String [] nodelist)   {
//        boolean[] output = new boolean[nodelist.length];
//        for (int i = 0; i < nodelist.length; i++) {
//        try{
//            call_target("reboot","remoteId",nodelist[i].trim().toString());
//           output[i]=true;
//        }catch(BuildException e){
//           output[i]=false;
//        }
//      }
//         return output;
//    }

/**
* Reset Input Node
*
* @param node   Input node as string
* @return        Output boolean table, true/false for each node
* @exception     Build Exception
*/
 final public boolean   resetNode(String node)   {
        try{
            System.out.println("SUNSPOT in ant reset node>>>>"+"  "+node);
            call_target("reboot","remoteId",node);
           return true;
        }catch(BuildException e){
           return false;
        }
    }



/**
* Getting property infos from Nodes
*
//* @param node    Input nodes as an array of strings
//* @return        Output string array that contains info for each node
//* @exception     Build Exception
//*/
// final public String[] info(String[] nodelist)  {
//
//        String[] output = new String[nodelist.length];
//        for (int i = 0; i < nodelist.length; i++) {
//        try{
//           String msg = call_target("info","remoteId",nodelist[i].trim().toString());
//           int l=msg.indexOf("[java] SPOT Client starting...");
//           int r=msg.lastIndexOf("[java]");
//           output[i]=msg.substring(l,r);
//        }catch(BuildException e){
//           output[i]=null;
//        }
//      }
//      return output;
//    }

 /**
* Getting property infos from Nodes
*
* @param node    Input node 
* @return        Output string containing info 
* @exception     Build Exception
*/
 final public String info(String node)   {

        String output = "";

        try{
           String msg = call_target("info","remoteId",node);
           int l=msg.indexOf("[java] SPOT Client starting...");
           int r=msg.lastIndexOf("[java]");
           output=msg.substring(l,r);
        }catch(BuildException e){
           throw new BuildException(e.toString());
        }
      
      return output;
    }

/**
* For each node listening, retrieves his neighbors and builds a graph
*
* @return       Output string array, an entry for each alive node that contains node name ":"
*               and then the one hop neighbors seperated by comma
 *              eg. 0014.4F01.0000.4A79: 0014.4F01.0000.4A78,0014.4F01.0000.4A73
* @exception     Build Exception
*/
//final public    String []  get_network() throws BuildException {
//          String msg=call_target("hello");
//          int l=msg.indexOf("[java] SPOT Client starting...");
//          int r=msg.lastIndexOf("[java]");
//          msg=msg.substring(l,r);
//          List node_names =  new LinkedList();
//          String[] lines=msg.split("\n");
//          int line=2;
//          while (line<lines.length){
//              if (lines[line].split("[.]").length==4){
//                   node_names.add(lines[line].substring(12));
//              }
//              line++;
//          }
//          String[] output=new String[node_names.size()];
//          for(int i=0;i<node_names.size();i++){
//              ant_project c=new ant_project(buildfilepath,workingPath);
//              try{
//                  String  msg1=c.get_routes(node_names.get(i).toString().trim());
//                  output[i]=node_names.get(i).toString().trim()+":"+msg1;
//              } catch (BuildException e) {
//                throw e;
//              }
//          }//for
//          return output;
//}


/**
* deploy a midlet to a node in a specific slot
*
* @param node   input node
* @param jar_name   path name of jar in the temp upload directore
* @param slot   application slot
* @param flash   if true deploy in parent isolate(starts with next reboot)
* @return       Output outcome
*
* @exception     Build Exception, IOEXception
*/
final public  boolean    flash_node(String  node, byte[] jarFI, boolean flash,String name) throws BuildException, IOException, Exception{

           //ant jar-deploy -DremoteID 0014.4F01.0000.6534
           //file preparing
           this.ant_project_reset();
           String working_dir="."+File.separatorChar;
           String new_dir=working_dir+UUID.randomUUID()+java.io.File.separator;
           String man_dir=new_dir+"resources"+java.io.File.separator+"MANIFEST"+java.io.File.separator;
           boolean dir1 = (new File(new_dir)).mkdirs();
           boolean dir2 = (new File(man_dir)).mkdirs();
           String jar_fpath=new_dir+"program.jar";
           File jarfile = new File(jar_fpath);
           OutputStream outf = new FileOutputStream(jarfile);
            outf.write(jarFI);
            outf.flush();
            outf.close();
            jar_fpath=jarfile.getAbsolutePath();
            boolean result=false;
                    try {
                      p.fireBuildStarted();
                      p.init();
                      helper = ProjectHelper.getProjectHelper();
                      p.addReference("ant.projectHelper", helper);
                      helper.parse(p, buildFile);
                      p.setUserProperty("remoteId",node );
                      p.setUserProperty("port","/dev/ttyACM0" );//-Dport="
                      p.setUserProperty("from.jar.file",jar_fpath ); //new_dir+jarFI.getName()
                      p.executeTarget("jar-deploy");
                      p.fireBuildFinished(null);
                       this.sendmsg("jar-deploy DONE"+" "+"remoteId"+" " +node+">>"+jar_fpath+">>"+out.toString());
                        result =true;
                    } catch (BuildException e) {
                      p.fireBuildFinished(e);
                      this.sendmsg("jar-deploy ERROR"+" "+"remoteId"+" " +node+">>"+jar_fpath+">>"+e.getMessage().toString());
                      result =false;
                    }
                   deleteFile(new_dir);
                    return result;
               
            };

          public static boolean deleteFile(String sFilePath)
        {
          File oFile = new File(sFilePath);
          if(oFile.isDirectory())
          {
            File[] aFiles = oFile.listFiles();
            for(File oFileCur: aFiles)
            {
               deleteFile(oFileCur.getAbsolutePath());
            }
          }
          return oFile.delete();
        }
//final public
//          void   run_midlet(String node,int slot) throws BuildException{
//            this.ant_project_reset();
//            try {
//              p.fireBuildStarted();
//              p.init();
//              helper = ProjectHelper.getProjectHelper();
//              p.addReference("ant.projectHelper", helper);
//              helper.parse(p, buildFile);
//              p.setUserProperty("remoteId",node);
//              p.setUserProperty("midlet",Integer.toString(slot) );
//              p.executeTarget("run");
//
//              p.fireBuildFinished(null);
//            } catch (BuildException e) {
//              p.fireBuildFinished(e);
//              this.sendmsg("run"+" "+"remoteId"+" " +node+" "+"midlet"+" " +slot+   ">>"+out.toString() +e.toString());
//              throw e;
//            }
//            this.sendmsg("run"+" "+"remoteId"+" " +node+" "+"midlet"+" " +slot+   ">>"+out.toString());
//             return  ;
//            }



/**
* Retrieve  application runnning, and status for input node
* @param node   input node
* @return       Output string array, an entry for each installed application in the form of
*               isolateid: ALIVE|HALTED eg.  g.sunspotworld.foo#1@0014.4F01.0000.4A79: ALIVE
* @exception     Build Exception
*/
//final public    String[]   get_applications_running(String node) throws BuildException {
//           String msg="";
//           try{
//            msg=call_target("getallappsstatus","remoteId",node);
//             }catch(BuildException e){
//                throw e;
//             }
//            int l=msg.indexOf("[java] Application status:");
//            int r=msg.lastIndexOf("[java] Exiting");
//            msg=msg.substring(l,r);
//
//            List isolates = new LinkedList();
//            String[] lines=msg.split("\n");
//           if (lines.length<2) return null;
//               int line=1;
//               while (line<lines.length){
//                   if (lines[line].contains(":"))
//                   isolates.add(lines[line].substring(12));
//               line++;
//            }
//
//            String[] output= new String[isolates.size()];
//            for (int i=0;i<isolates.size();i++)
//                 output[i]=isolates.get(i).toString().trim();
//            return output;
//}
/**
* Retrieve  application installed  for input node
* @param node   input node
* @return       Output string array, an entry for each installed application in the form of
*               isolateid: ALIVE|HALTED eg.  g.sunspotworld.foo#1@0014.4F01.0000.4A79: ALIVE
* @exception     Build Exception
*/
//final public    String[]   get_applications(String node) throws BuildException {
//           String msg="";
//           try{
//            msg=call_target("getavailablesuites","remoteId",node);
//             }catch(BuildException e){
//                throw e;
//             }
//            msg="";
//            msg=out.toString();
//            int l=msg.indexOf("[java] Available suites:");
//            int r=msg.lastIndexOf("[java] Exiting");
//            msg=msg.substring(l,r);
//
//            List suites = new LinkedList();
//            String[] lines=msg.split("\n");
//           if (lines.length<2) return null;
//               int line=1;
//               while (line<lines.length){
//                   if (lines[line].contains("Id") &&lines[line].contains(":") )
//                   suites.add(lines[line].substring((lines[line].indexOf(":")+1)));
//               line++;
//            }
//
//            String[] output= new String[suites.size()];
//            for (int i=0;i<suites.size();i++){
//                output[i]=suites.get(i).toString();
//
//            }
//
//            return output;
//}
//-------------------------untested bellow---------------------------------------------------------
//
//final public void   stop_application(String node,String isolateid) throws BuildException{
//            this.ant_project_reset();
//            try {
//              p.fireBuildStarted();
//              p.init();
//              helper = ProjectHelper.getProjectHelper();
//              p.addReference("ant.projectHelper", helper);
//              helper.parse(p, buildFile);
//              p.setUserProperty("remoteId",node);
//              p.setUserProperty("isolateId",isolateid );
//              p.executeTarget("stopapp");
//
//              p.fireBuildFinished(null);
//            } catch (BuildException e) {
//              p.fireBuildFinished(e);
//              throw e;
//            }
//
//
//
//                return  ;
//            }
//
//final public void   resume_application(String node,String isolateid) throws BuildException{
//            this.ant_project_reset();
//            try {
//              p.fireBuildStarted();
//              p.init();
//              helper = ProjectHelper.getProjectHelper();
//              p.addReference("ant.projectHelper", helper);
//              helper.parse(p, buildFile);
//              p.setUserProperty("remoteId",node);
//              p.setUserProperty("isolateId",isolateid );
//              p.executeTarget("resumeapp");
//
//              p.fireBuildFinished(null);
//            } catch (BuildException e) {
//              p.fireBuildFinished(e);
//              throw e;
//            }
//             return  ;
//}
//
//final public void   pause_application(String node,String isolateid) throws BuildException{
//            this.ant_project_reset();
//            try {
//              p.fireBuildStarted();
//              p.init();
//              helper = ProjectHelper.getProjectHelper();
//              p.addReference("ant.projectHelper", helper);
//              helper.parse(p, buildFile);
//              p.setUserProperty("remoteId",node);
//              p.setUserProperty("isolateId",isolateid );
//              p.executeTarget("pauseapp");
//
//              p.fireBuildFinished(null);
//            } catch (BuildException e) {
//              p.fireBuildFinished(e);
//              throw e;
//            }
//
//
//
//                return  ;
//            }
//
//final public void   start_application(String node,String uri) throws BuildException{
//            this.ant_project_reset();
//            try {
//              p.fireBuildStarted();
//              p.init();
//              helper = ProjectHelper.getProjectHelper();
//              p.addReference("ant.projectHelper", helper);
//              helper.parse(p, buildFile);
//              p.setUserProperty("remoteId",node);
//              p.setUserProperty("uri",uri );
//              p.executeTarget("startapp");
//
//              p.fireBuildFinished(null);
//            } catch (BuildException e) {
//              p.fireBuildFinished(e);
//              throw e;
//            }
//
//
//
//                return  ;
//            }
//
//
// public void   undeploy_application(String node,String uri) throws BuildException{
//            this.ant_project_reset();
//            try {
//              p.fireBuildStarted();
//              p.init();
//              helper = ProjectHelper.getProjectHelper();
//              p.addReference("ant.projectHelper", helper);
//              helper.parse(p, buildFile);
//              p.setUserProperty("remoteId",node);
//              p.setUserProperty("uri",uri );
//              p.executeTarget("undeploy");
//              p.fireBuildFinished(null);
//            } catch (BuildException e) {
//              p.fireBuildFinished(e);
//              throw e;
//            }
//
//
//
//                return  ;
//            }
//
//
//
//public void   setTime(String node) throws BuildException{
//            this.ant_project_reset();
//            try {
//              p.fireBuildStarted();
//              p.init();
//              helper = ProjectHelper.getProjectHelper();
//              p.addReference("ant.projectHelper", helper);
//              helper.parse(p, buildFile);
//              p.setUserProperty("remoteId",node);
//              p.executeTarget("settime");
//              p.fireBuildFinished(null);
//               this.sendmsg("setime"+" "+"remoteId"+" " +node +   ">>"+out.toString());
//            } catch (BuildException e) {
//               this.sendmsg("setime"+" "+"remoteId"+" " +node +   ">>"+out.toString()+e.toString() +e.toString());
//              p.fireBuildFinished(e);
//              throw e;
//            }
//               return  ;
//            }
//
//public void   setSystemProperty(String node,String key,String value) throws BuildException{
//            this.ant_project_reset();
//            try {
//              p.fireBuildStarted();
//              p.init();
//              helper = ProjectHelper.getProjectHelper();
//              p.addReference("ant.projectHelper", helper);
//              helper.parse(p, buildFile);
//              p.setUserProperty("remoteId",node);
//              p.setUserProperty("key",key);
//              p.setUserProperty("value",value);
//              p.executeTarget("set-system-property");
//              p.fireBuildFinished(null);
//                this.sendmsg("set-system-property"+" "+key+" " +value +   ">>"+out.toString());
//            } catch (BuildException e) {
//              p.fireBuildFinished(e);
//              this.sendmsg("set-system-property"+" "+key+" " +value +   ">>"+out.toString()+e.toString());
//              throw e;
//            }
//               return  ;
//            }
//
//
//
//
//




     /**
     * Reseting Constructor
     *
     * @param path  Path of the Sunspot buil.xml  C:\\Sun\\SunSPOT\\sdk\\build.xml
     */
     public void ant_project_reset(){
            p = new Project();
            p.setUserProperty("ant.file", buildFile.getAbsolutePath());
            consoleLogger = new DefaultLogger();
            out = new ByteArrayOutputStream();
            ps_out = new PrintStream(out);
            error = new ByteArrayOutputStream();
            ps_error = new PrintStream(error);
            consoleLogger.setOutputPrintStream(ps_out);
            consoleLogger.setErrorPrintStream(ps_error);
            consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
            p.addBuildListener(consoleLogger);
        }

     public void sendmsg(String msg){
           msg=msg+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n\n\n";
           System.out.println("SUNSPOT MSG>>"+msg);
     };


    Runnable host = new Runnable() {
            @Override
            public void run() {
             try {
              p.fireBuildStarted();
              p.init();
              helper = ProjectHelper.getProjectHelper();
              p.addReference("ant.projectHelper", helper);
              helper.parse(p, buildFile);
              p.setUserProperty("port", "/dev/ttyACM1");
              p.executeTarget("host-run");
              p.fireBuildFinished(null);

            } catch (BuildException e) {
               log.debug("SUNSPOT>>>>"+e.getMessage());
               p.fireBuildFinished(e);
            }
          }
    };


    public ant_project() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public  void call_host() throws IOException {
        FeedbackManager mman=FeedbackManager.getInstance();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("SchedulerService-Thread %d").build());
        scheduler.execute(host);
        boolean inspecOutput = true;
            String msg="";
             while(inspecOutput)
             {
                out_msg=out.toString();
                 msg=out.toString();
                 List<String> msgs= null;
                 try {
                     msgs = getMessages(out.toString());
                 } catch (IOException e) {
                     log.debug(e.toString());  //To change body of catch statement use File | Settings | File Templates.
                 }
                 File file = new File("./host.log");
                 FileWriter writer = new FileWriter(file, true);
                 out.reset();
                 for(String msgg :msgs){
                        String [] part=msgg.split(":");
                        SunspotDevice sd=mman.getDevice(part[0]);  //to change...
                        log.debug("SUNSPOT>>"+ part[0] +"-"+part[1]);
                        if (sd==null) continue;
                        writer.write( new Date(System.currentTimeMillis()).toString() +": "+part[0]+":"+part[1]+"\n");
                        writer.flush();
                        sd.logmsg(part[1]);
                  }

                 writer.close();
                 try {
                     Thread.sleep(2000);
                 } catch (InterruptedException e) {
                     e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                 }
             }


    }

 public List<String>  getMessages(String txt) throws IOException {
        //log.debug("SUNSPOT>>"+ txt);
        List<String> answer=new ArrayList<String>(100);
        String[] msgs=txt.split("[java] ");
        for(String str:msgs){
            String[] parts=str.split(":");
            if (parts !=null && parts[0].contains("SunspotHostMsg") ){
                answer.add(parts[2]+":"+parts[3]);
            }
        }

        return answer;
    }
 }//class
