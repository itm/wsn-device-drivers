package de.uniluebeck.itm.wsn.devicedrivers.sunspot;

import org.apache.tools.ant.BuildException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class TestBed  {
    private String build_path;
    private String [] network;
    private HashMap snodes;
    private ant_project p;
    private int reqId=0;
    private FeedbackManager mman;
    private static final Logger log = LoggerFactory.getLogger(FeedbackManager.class);

    private static transient volatile TestBed instance;

      public synchronized static TestBed getInstance(){
        if(instance==null)
            synchronized(TestBed.class){
            if(instance==null){
                instance = new TestBed();
            }
        }
        return instance;
    }


    private TestBed(){
        build_path="Sun"+ File.separatorChar +"sdk"+ File.separatorChar+"build.xml";
        snodes=new HashMap();
        mman=FeedbackManager.getInstance();
    }

    public synchronized boolean  resetNode(String node) {
        return ((Snode)snodes.get(node)).resetNode();
    }

    public synchronized void  addNode(String node,SunspotDevice SD) {
        mman.addDevice(SD);
        snodes.put(node,new Snode(build_path,node));
        if (SD.isNodeGateway()){
            mman.start();

             log.debug("SUNSPOT>>> Host started");
        }
    }





    public synchronized boolean   isNodeAlive(String  node)   {
        boolean output=((Snode)snodes.get(node)).isAlive();
        return output;
    }

     public synchronized boolean   jar_deploy(String  node,byte[] jar,String name) throws IOException, Exception {
       Snode nd=((Snode)snodes.get(node));
       boolean result;
       if (nd==null) return false; // or throw exception...
            try{
                result=nd.flashJar(jar,name);
            }catch(BuildException e){
                 result =false;
            }
       return result;
    }



    //===================================

//     public synchronized boolean enableNode(String node)   {
//             if (snodes.containsKey(node)){
//                 if (enabled_snodes.containsKey(node)==false){
//                     enabled_snodes.put(node,snodes.get(node));
//                     ((Snode)snodes.get(node)).enable();
//
//                }
//                return true;
//            }
//            else{
//                 return false;
//            }
//
//    }

//       public  boolean disableNode(String node)   {
//             if (snodes.containsKey(node)){
//                 if (enabled_snodes.containsKey(node)==true){
//                     enabled_snodes.remove(node);
//                     ((Snode)snodes.get(node)).disable();
//                     ((Snode)snodes.get(node)).resetStartTime();
//                }
//                return true;
//            }else{
//                 return false;
//            }
//
//    }

//
//    private TestBed(String w_dir,FileItem defapp,String[]nodes){
//
//        working_dir=w_dir;
//        //build_path=working_dir+File.separatorChar+path;
//        build_path="C:\\Sun\\SunSPOT\\sdk\\build.xml";
//        default_app=defapp;
//        snodes=new HashMap();
//        enabled_snodes=new HashMap();
//        p=new ant_project(build_path,working_dir);
//        for (String node:nodes){
//            snodes.put(node,new Snode(build_path,w_dir,node));
//            enabled_snodes.put(node,snodes.get(node)); //has to enable all from the start?
//        }
//    }
//
//    // from static
//    public  String[] getNetwork()   {
//        network=new String[snodes.size()];
//        int i=0;
//        for (Object nd:snodes.values()){
//            network[i++]=((Snode)nd).getMac();
//        }
//        return network;
//    }


//    public  String[] getRealNetwork()   {
//         return p.get_network();
//    }



//      public  void defineNetwork(String [] nodes)   {
//        enabled_snodes.clear();
//        for(int i=0;i<nodes.length;i++){
//            String nd=nodes[i];
//            if (snodes.containsKey(nd)){
//                 enabled_snodes.put(nd,snodes.get(nd));
//                 ((Snode)snodes.get(nd)).enable();
//            }
//
//        }
//    }



//       public  boolean [] enableNodes(String [] nodes)   {
//           boolean [] answer = new boolean[nodes.length];
//
//           enabled_snodes.clear();
//           int cnt=0;
//           for (String node:nodes){
//             if (snodes.containsKey(node)){
//                 if (enabled_snodes.containsKey(node)==false){
//                     enabled_snodes.put(node,snodes.get(node));
//                     ((Snode)snodes.get(node)).enable();
//
//                }
//                answer[cnt]= true;
//            }
//            else{
//                answer[cnt]= false;
//            }
//            cnt++;
//           }
//
//           for (Object node:snodes.keySet()){
//               if (enabled_snodes.containsKey((String) node)==false){
//                   this.disableNode((String) node);
//               }
//
//           }
//           return answer;
//    }







//       public synchronized void   jar_deploy(String  node,FileItem jar,boolean flash) throws IOException, Exception {
//       Snode nd=((Snode)snodes.get(node));
//       if (nd==null) return; // or throw exception...
//            try{
//                if (flash==true)nd.flashJar(jar);
//                else            nd.deployJar(jar);
//            }catch(BuildException e){
//               throw e;
//            }
//
//    }


//
//    public boolean[]   areNodesAlive(String [] nodes)   {
//        boolean[] output=new boolean[nodes.length];
//        for (int i=0;i<nodes.length;i++){
//            output[i]=((Snode)snodes.get(nodes[i])).isAlive();
//        }
//        return output;
//    }

  



//    public String   info(String node) {
//        String output= "";
//        String out=((Snode)snodes.get(node)).getInfo();
//        if (out!=null) output=out;
//        else output="null";
//        return output;
//    }

//
//    public String[]   getApplications(String node) {
//        Snode nd=((Snode)snodes.get(node));
//        if (nd==null) return null;
//        String[] output=null;
//          try{
//            if(nd.syncApplications()==true)
//             output= nd.getApplications();
//        }catch(BuildException e){
//           output=null;
//        }
//         return output;
//    }
//
//    public String[]   getApplicationsRunning(String node) {
//        Snode nd=((Snode)snodes.get(node));
//        if (nd==null) return null;
//        String[] output=null;
//          try{
//            if(nd.syncApplicationsRunnning()==true)
//                output= nd.getApplicationsRunnning();
//            }catch(BuildException e){
//           output=null;
//        }
//         return output;
//    }




//     public boolean   stop_application(String node,String isolateid) throws BuildException {
//       Snode nd=((Snode)snodes.get(node));
//       if (nd==null) return false; // or throw exception...
//       try{
//          //if  (nd.syncApplicationsRunnning()==true)
//          return nd.stopApplication(isolateid);
//         // return false;
//       }catch(BuildException e){
//          throw e;
//
//       }
//    }
//
//       public boolean   startApplication(String node,String uri) throws BuildException {
//           Snode nd=((Snode)snodes.get(node));
//           if (nd==null) return false; // or throw exception...
//       try{
//         //  if (nd.syncApplications()==true)
//                return nd.startApplication(uri);
//         //  else return false;
//       }catch(BuildException e){
//          throw e;
//       }
//    }
//
//
//     public boolean   undeploy_application(String node,String uri) throws BuildException {
//            Snode nd=((Snode)snodes.get(node));
//            if (nd==null) return false; // or throw exception...
//       try{
//           //if (nd.syncApplications()==true)
//                return nd.undeploySuite(uri);
//          // else return false;
//       }catch(BuildException e){
//          throw e;
//       }
//    }
//
//
//     public boolean   pause_application(String node,String isolateid) throws BuildException {
//      Snode nd=((Snode)snodes.get(node));
//      if (nd==null) return false; // or throw exception...
//       try{
//           // nd.syncApplicationsRunnning();
//            return nd.pauseApplication(isolateid);
//       }catch(BuildException e){
//          throw e;
//       }
//
//    }
//
//
//
//     public boolean   resume_application(String node,String isolateid) throws BuildException {
//       Snode nd=((Snode)snodes.get(node));
//       if (nd==null) return false; // or throw exception...
//       try{
//          // nd.syncApplicationsRunnning();
//            return nd.resumeApplication(isolateid);
//       }catch(BuildException e){
//          throw e;
//       }
//    }

// public boolean[]   setup_nodes(String[]  nodes) throws IOException, Exception {
//     boolean [] answer=new boolean[nodes.length];
//     Snode nd=null;
//       int cnt=0;
//       for(String node:nodes){
//            nd=((Snode)snodes.get(node));
//
//            if (nd==null){
//                answer[cnt++]=false;
//                continue;
//            }
//
//            nd.enable();
//            try{
//
//                if (nd.setTime()==false) {
//                         answer[cnt++]=false;
//                         continue;
//                }
//                if (nd.setSystemProperty("starttime", "ndef")==false) {
//                         answer[cnt++]=false;
//                         continue;
//                }
//               if (nd.setup(this.default_app)==false){
//                         answer[cnt++]=false;
//                         continue;
//                }
//            }catch(BuildException e){
//                throw e;
//             }
//            answer[cnt++]=true;
//       }
//      return answer;
//    }

//  public boolean   deployApplication(String  node,FileItem jar) throws IOException, Exception {
//       Snode nd=((Snode)snodes.get(node));
//       if (nd==null) return false; // or throw exception...
//            try{
//                nd.deployApplication(jar);
//                return true;
//            }catch(BuildException e){
//                return false;
//                //throw e;
//            }
//
//    }
//
//  public void   undeployApplication(String  node) throws IOException, Exception {
//       Snode nd=((Snode)snodes.get(node));
//       if (nd==null) return; // or throw exception...
//            try{
//                nd.undeployApplication();
//            }catch(BuildException e){
//               throw e;
//            }
//
//    }
//
//
//
//
//
//  public boolean[]   setStartTime(XMLGregorianCalendar time) throws IOException, Exception {
//      boolean[] answer=new boolean[this.enabled_snodes.size()];
//      int cnt=0;
//      for(Object node:this.enabled_snodes.keySet().toArray()){
//        Snode nd=((Snode)snodes.get((String)node));
//         try{
//                nd.setTime();
//                answer[cnt]= nd.setStartTime(time);
//            }catch(BuildException e){
//                answer[cnt]=false;
//                //throw e;
//            }
//      cnt++;
//      }
//      return answer;
//    }

//
//    public boolean [] areNodesValid(String[] nodes) throws Exception {
//        boolean [] answer=new boolean[nodes.length];
//        for (int i=0;i<nodes.length;i++){
//            answer[i]=this.enabled_snodes.containsKey(nodes[i]);
//
//        }
//        return answer;
//    }

//    public boolean areNodesValid(String[] nodes) throws Exception {
//        boolean flag=true;
//        for (int i=0;i<nodes.length;i++){
//            if (this.enabled_snodes.containsKey(nodes[i])==false){
//                throw new Exception("Unkown NodeID:"+nodes[i]);
//            }
//        }
//        return flag;
//    }

//        public boolean areNodeValid(String node)   {
//            return this.enabled_snodes.containsKey(node) ;
//        }


//    //Has to be extented ....
//    public String getPropertyValueOf(String node, String property) {
//        return info(node);
//    }


//   public boolean[]   resetNodes(String [] nodes) {
//        boolean[] output=new boolean[nodes.length];
//        for (int i=0;i<nodes.length;i++){
//             output[i]=((Snode)snodes.get(nodes[i])).resetNode();
//        }
//        return output;
//    }


//   public boolean[]   resetSetup() throws Exception {
//        boolean[] output=new boolean[snodes.size()];
//        int cnt=0;
//        for (Object nd:snodes.values()){
//            ((Snode)nd).undeployApplication();
//            ((Snode)nd).resetStartTime();
//             output[cnt++]=((Snode)nd).setup(this.default_app);
//        }
//       return output;
//    }



//      public String[]    getEnabledNodes() {
//          if (this.enabled_snodes.size()==0) return null;
//          String nodes[]=new String[this.enabled_snodes.size()];
//          int cnt=0;
//          for (Object nd:this.enabled_snodes.keySet())
//                nodes[cnt++]= (String) nd;
//         return nodes;
//    }



}

