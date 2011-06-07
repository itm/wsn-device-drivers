package de.uniluebeck.itm.wsn.devicedrivers.sunspot;

import org.apache.tools.ant.BuildException;

import java.io.IOException;

public class Snode
{
private String mac_address;
private ant_project p;


 /**
 * Constructor
 *
 * @param build_path  Path of the Sunspot buil.xml C:\\Sun\\SunSPOT\\sdk\\build.xml
 * @param mac  MAc address of sensor node
 */
public Snode(String build_path,String mac){
    p=new ant_project(build_path);
    mac_address=mac;
}

/**
* Returns the mac address
*
* @return        mac address string
*/
public String getMac(){
    return mac_address;
}

/**
* Returns the urn address
*
* @return        mac address string
*/
//public String getUrn(){
//    return urn;
//}

/**
* Enable node (in testbed abstraction)
*
*/
//public void enable(){
//   enabled=true;
//}


/**
* Disable node (in testbed abstraction)
//*
//*/
//public void disable(){
//   enabled=false;
//   //flash with null app
//}



/**
* Check in real testbed is node responds
*
* @return        true/false
*/
public boolean isAlive(){
    try{
      String info=p.info(mac_address);
      return true;
    }catch(BuildException e){
      return false;
    }
}



/**
* Retrieve info string from SunSpot
*
* @return  info strin of null
*/
//public String getInfo(){
//    try{
//      info=p.info(mac_address);
//      return info;
//    }catch(BuildException e){
//      return null;
//    }
//}

/**
* Reset the SunSpot
*
* @return  true/false
*/
public boolean resetNode(){
    try{
        System.out.println("SUNSPOT node reset node>>>>"+"  "+this.mac_address);
      return p.resetNode(mac_address);
    }catch(BuildException e){
      return false;
    }
}

/**
* Retrieve the neighbors of the node from network
*
//* @return  String array with neighbors macs or null
//*/
//public String [] getNeighbors(){
//    try{
//      String str=p.get_routes(mac_address);
//      return str.split(",");
//    }catch(BuildException e){
//      return null;
//    }
//}


/**
* Deploy Jar in suite collection of Sunspot
* @param jar Filename of the jar in the deploy directory
* @return  true/false
//*/
//public boolean deployJar(byte[] jar,String name) throws Exception{
//    try{
//      p.flash_node(mac_address, jar, false,name); //false is for deploy only
//      this.syncApplications();
//      return true;
//    }catch(BuildException e){
//         return false;
//    } catch (IOException e) {
//         return false;
//    }
//}

/**
* Deploy Jar in suite collection of Sunspot
* This app will start in main isolate in the next reboot
 *
* @param jar Filename of the jar in the deploy directory
* @return  true/false
*/
public boolean flashJar( byte[] jar,String name) throws Exception{
    boolean result;
    try{
      result=p.flash_node(mac_address,  jar, true,name);
      //this.resetNode();   //reset node to start app
      //this.syncApplications(); //sync application suites
      //this.syncApplicationsRunnning(); //sync application running
    return result;
    }catch(BuildException e){
         return false;
    } catch (IOException e) {
         return false;
    }

}


/**
* Retrieve/update from sunspot the uploaded suites
//* @return  true/false
//*/
//public boolean syncApplications(){
//    try{
//      String [] s=p.get_applications(mac_address);
//      suites.clear();
//      user_suites.clear();
//      String suite="";
//      int cnt=0;
//      for (int i=s.length-1;i>=0;i--){
//          suite=s[i].trim();
//          suites.put(suite, new suite(mac_address,suite,p));
//          cnt++;
//          if (cnt>=2){
//              user_suites.push(suites.get(suite));
//
//          }
//
//      }
//
//
//      return true;
//    }catch(BuildException e){
//      return false;
//    }
//}

/**
* Return the applications uploads
* @return  String array with suite names
*/
//public String [] getApplications(){
//    String [] apps=new String[suites.size()];
//    Set set = suites.entrySet();
//    Iterator i = set.iterator();
//    int cnt=0;
//    while(i.hasNext()){
//      Map.Entry me = (Map.Entry)i.next();
//      apps[cnt++]=((suite)me.getValue()).getUri() ;
//    }
//    return apps;
//}

/**
* Retrieve/update from sunspot the isolates
* @return  true/false
//*/
//public boolean syncApplicationsRunnning(){
//     try{
//      String [] s=p.get_applications_running(mac_address);
//      isolates.clear();
//      String isolate="";
//      for (int i=0;i<s.length;i++){
//          isolate=s[i].trim();
//          isolates.put(isolate.split(":")[0], new isolate(mac_address,isolate,p));
//      }
//      return true;
//    }catch(BuildException e){
//      return false;
//    }
//}

/**
* Return the isolated running
* @return  String array with isolate names
//*/
//public String [] getApplicationsRunnning(){
//    String [] appsr=new String[isolates.size()];
//    Set set = isolates.entrySet();
//    Iterator i = set.iterator();
//    int cnt=0;
//    while(i.hasNext()){
//      Map.Entry me = (Map.Entry)i.next();
//      //if (((isolate)me.getValue()).isMaster())continue;
//      appsr[cnt++]=((isolate)me.getValue()).getIsolateId() ;
//    }
//    return appsr;
//}

/**
* UnDeploy Jar from suite collection of Sunspot

* @param jar_name Filename of the jar suite uri
* @return  true/false
//*/
//public  boolean undeploySuite(String uri){
//    if (suites.containsKey(uri)){
//         boolean status=((suite)suites.get(uri)).undeploy();
//         if (status==true) this.syncApplications();
//         return status;
//    }else
//        return false;
//
//}

/**
* Start Suite from suite collection of Sunspot

* @param  suite uri
//* @return  true/false
//*/
//public boolean startApplication(String uri){
//    if (suites.containsKey(uri)){
//         boolean status=((suite)suites.get(uri)).start();
//         if (status==true) this.syncApplicationsRunnning();
//         return status;
//    }else
//        return false;
//}


/**
* Stop isolate from isolate collection of Sunspot
* @param  isolate id
* @return  true/false
*/
//public boolean stopApplication(String isolateid){
//    if (isolates.containsKey(isolateid)){
//         isolate is=(isolate)isolates.get(isolateid);
//         boolean status=false;
//         if (is.isAlive()){
//            status=is.stop();
//            if (status==true) this.syncApplicationsRunnning();
//         }
//         return status;
//    }else
//        return false;
//}
 

/**
* Resume isolate from isolate collection of Sunspot
* @param  isolate id
* @return  true/false
*/
//public boolean resumeApplication(String isolateid){
//     if (isolates.containsKey(isolateid)){
//          isolate is=(isolate)isolates.get(isolateid);
//         boolean status=false;
//         if (is.isPaused()){
//            status=is.resume();
//            if (status==true)
//                this.syncApplicationsRunnning();
//         }
//         return status;
//    }else
//        return false;
//}

/**
* Pause isolate from isolate collection of Sunspot
* @param  isolate id
* @return  true/false
*/
//public boolean pauseApplication(String isolateid){
//  if (isolates.containsKey(isolateid)){
//         isolate is=(isolate)isolates.get(isolateid);
//         boolean status=false;
//         if (is.isAlive()){
//            status=is.pause();
//            if (status==true)
//                this.syncApplicationsRunnning();
//         }
//         return status;
//    }else
//        return false;
//
//}

/**
* Set Time
* @return  true/false
*/
//public boolean setTime(){
//   try{
//        p.setTime(this.getMac());
//        return true;
//    }catch(BuildException e){
//        return false;
//    }
//}


/**
* Set System Property
* @param  key
* @param  value
* @return  true/false
*/
//public boolean setSystemProperty(String key, String value){
//   try{
//        p.setSystemProperty(this.getMac(),key,value);
//        return true;
//    }catch(BuildException e){
//        return false;
//    }
//}
//
/////**
//* Set Start Time
//* @param  key
//* @param  value
//* @return  true/false
//*/
//public boolean setStartTime(long time){
//   try{
//        p.setSystemProperty(this.getMac(),"starttime",String.valueOf(time));
//        return true;
//    }catch(BuildException e){
//        return false;
//    }
//}

//public boolean setup(FileItem defaultApp ) throws Exception{
//    return     this.flashJar(defaultApp);
//}

//public boolean deployApplication(FileItem userApp) throws Exception{
//   this.undeployApplication();
//   boolean answer=this.deployJar(userApp);
//   this.syncApplications();
//   return answer;
//}
//
//public boolean undeployApplication() throws Exception{
//    if (user_suites.size()==0) return true;
//    this.resetStartTime();
//    return  this.undeploySuite( ((suite)  user_suites.pop()).getUri());
//}
//
//    boolean setStartTime(XMLGregorianCalendar time) {
//       if (time==null)  return  this.setSystemProperty("starttime", String.valueOf(System.currentTimeMillis()));
//       return  this.setSystemProperty("starttime", String.valueOf(time.toGregorianCalendar().getTimeInMillis()));
//    }
//
//    boolean resetStartTime() {
//       return  this.setSystemProperty("starttime", "ndef");
//    }

}