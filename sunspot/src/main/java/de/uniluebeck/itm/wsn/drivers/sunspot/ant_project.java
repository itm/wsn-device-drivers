package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.common.collect.Multimap;
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
    private File buildFile;
    private Project p;
    private DefaultLogger consoleLogger;
    private ByteArrayOutputStream out, error;
    private PrintStream ps_out, ps_error;
    private ProjectHelper helper;
    private String out_msg;
    private static final Logger log = LoggerFactory.getLogger(ant_project.class);
    private String sunspotPort;
    private String tempDirectory;
    boolean inspecOutput = true;

    public ant_project(String bpath, String port, String tmpDirectory) {
        this.sunspotPort=port;
        this.tempDirectory=tmpDirectory;
        buildFile = new File(bpath);
        p = new Project();
        p.setUserProperty("ant.file", buildFile.getAbsolutePath());
        System.out.println(buildFile.getAbsolutePath());
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


    final public String call_target(String target) {
        this.ant_project_reset();
        try {
            p.fireBuildStarted();
            p.init();
            helper = ProjectHelper.getProjectHelper();
            p.addReference("ant.projectHelper", helper);
            helper.parse(p, buildFile);
            p.setUserProperty("port", this.sunspotPort);                                                                                 //" -Dport=ttyACM0"
            p.executeTarget(target);
            p.fireBuildFinished(null);
        } catch (BuildException e) {
            this.sendmsg(target + ">>" + out.toString());

            p.fireBuildFinished(e);
            throw e;
        }
        out_msg = out.toString();
        this.sendmsg(target + ">>" + out.toString());
        return out_msg;
    }

    private String call_target(String target, String property, String value) throws BuildException {
        this.ant_project_reset();
        try {
            p.fireBuildStarted();
            p.init();
            helper = ProjectHelper.getProjectHelper();
            p.addReference("ant.projectHelper", helper);
            helper.parse(p, buildFile);
            p.setUserProperty(property, value.substring(0, value.length()));
            p.setUserProperty("port", this.sunspotPort);
            p.executeTarget(target);
            p.fireBuildFinished(null);
        } catch (BuildException e) {
            p.fireBuildFinished(e);
            this.sendmsg(target + " " + property + " " + value + ">>" + out.toString() + e.toString());
            throw e;
        }
        out_msg = out.toString();
        this.sendmsg(target + " " + property + " " + value + ">>" + out.toString());
        return out_msg;
    }


    final public String[] hello() {
        String msg;
        try {
            msg = call_target("hello");
        } catch (BuildException e) {
            p.fireBuildFinished(e);
            throw e;
        }
        int l = msg.indexOf("[java] SPOT Client starting...");
        if (l < 0) return null;
        int r = msg.lastIndexOf("[java]");
        msg = msg.substring(l, r);
        List node_names = new LinkedList();
        String[] lines = msg.split("\n");
        int line = 2;
        while (line < lines.length) {
            if (lines[line].split("[.]").length == 4 && lines[line].contains("[BasestationManager]") == false && lines[line].contains("IP") == false) {
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



    final public boolean resetNode(String node) {
        try {
            System.out.println("SUNSPOT in ant reset node>>>>" + "  " + node);
            call_target("reboot", "remoteId", node);
            return true;
        } catch (BuildException e) {
            return false;
        }
    }

    final public String info(String node) {
        String output = "";

        try {
            String msg = call_target("info", "remoteId", node);
            int l = msg.indexOf("[java] SPOT Client starting...");
            int r = msg.lastIndexOf("[java]");
            output = msg.substring(l, r);
        } catch (BuildException e) {
            throw new BuildException(e.toString());
        }

        return output;
    }

    final public boolean flash_node(String node, byte[] jarFI, boolean flash) throws BuildException, IOException, Exception {

        //ant jar-deploy -DremoteID 0014.4F01.0000.6534
        //file preparing
        this.ant_project_reset();
        String working_dir = this.tempDirectory + File.separatorChar;
        String new_dir = working_dir + UUID.randomUUID() + java.io.File.separator;
        String man_dir = new_dir + "resources" + java.io.File.separator + "MANIFEST" + java.io.File.separator;
        boolean dir1 = (new File(new_dir)).mkdirs();
        boolean dir2 = (new File(man_dir)).mkdirs();
        String jar_fpath = new_dir + "program.jar";
        File jarfile = new File(jar_fpath);
        OutputStream outf = new FileOutputStream(jarfile);
        outf.write(jarFI);
        outf.flush();
        outf.close();
        jar_fpath = jarfile.getAbsolutePath();
        boolean result = false;
        try {
            p.fireBuildStarted();
            p.init();
            helper = ProjectHelper.getProjectHelper();
            p.addReference("ant.projectHelper", helper);
            helper.parse(p, buildFile);
            p.setUserProperty("remoteId", node);
            p.setUserProperty("port", this.sunspotPort);//-Dport="
            p.setUserProperty("from.jar.file", jar_fpath); //new_dir+jarFI.getName()
            p.executeTarget("jar-deploy");
            p.fireBuildFinished(null);
            this.sendmsg("jar-deploy DONE" + " " + "remoteId" + " " + node + ">>" + jar_fpath + ">>" + out.toString());
            result = true;
        } catch (BuildException e) {
            p.fireBuildFinished(e);
            this.sendmsg("jar-deploy ERROR" + " " + "remoteId" + " " + node + ">>" + jar_fpath + ">>" + e.getMessage().toString());
            result = false;
        }
        deleteFile(new_dir);
        return result;

    }

    public static boolean deleteFile(String sFilePath) {
        File oFile = new File(sFilePath);
        if (oFile.isDirectory()) {
            File[] aFiles = oFile.listFiles();
            for (File oFileCur : aFiles) {
                deleteFile(oFileCur.getAbsolutePath());
            }
        }
        return oFile.delete();
    }


    public void ant_project_reset() {
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

    public void sendmsg(String msg) {
        msg = msg + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n\n\n";
        System.out.println("SUNSPOT MSG>>" + msg);
    }

    ;


    Runnable host = new Runnable() {
        @Override
        public void run() {
            try {
                p.fireBuildStarted();
                p.init();
                helper = ProjectHelper.getProjectHelper();
                p.addReference("ant.projectHelper", helper);
                helper.parse(p, buildFile);
                p.setUserProperty("port", sunspotPort);
                p.executeTarget("host-run");
                p.fireBuildFinished(null);
            } catch (BuildException e) {
                log.debug("SUNSPOT HOST APPLICATION>>>>" + e.getMessage());
                inspecOutput = false;
                p.fireBuildFinished(e);
            }
        }
    };

    public void call_host(Multimap<String, SunspotBaseStationListener> listeners) throws IOException {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("SchedulerService-Thread %d").build());
        scheduler.execute(host);

        String msg = "";
        while (inspecOutput) {
            out_msg = out.toString();
            //log.debug(out_msg);
            msg = out.toString();
            List<String> msgs = null;
            try {
                msgs = getMessages(out.toString());
            } catch (IOException e) {
                log.debug(e.toString());  //To change body of catch statement use File | Settings | File Templates.
            }
            out.reset();
            for (String msgg : msgs) {
                String[] part = msgg.split(":");
                log.debug("SUNSPOT>>" + part[0] + "-" + part[1]);
                Collection<SunspotBaseStationListener> sd = listeners.get(part[0]);
                for (SunspotBaseStationListener list : sd) {
                    list.messageReceived(part[1].getBytes());
                }
                if (sd == null) continue;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public List<String> getMessages(String txt) throws IOException {
        //log.debug("SUNSPOT>>"+ txt);
        List<String> answer = new ArrayList<String>(100);
        String[] msgs = txt.split("[java] ");
        for (String str : msgs) {
            String[] parts = str.split(":");
            if (parts != null && parts[0].contains("SunspotHostMsg")) {
                answer.add(parts[2] + ":" + parts[3]);
            }
        }
        return answer;
    }
}//class
