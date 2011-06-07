 EDemoBoard.getInstance().getLEDs()[3].setOn();
 EDemoBoard.getInstance().getLEDs()[3].setRGB(0, 100, 255);
    notifyDestroyed();                      // cause the MIDlet to exit
    }

    protected void pauseApp() {
        // This is not currently called by the Squawk VM
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        
    }
}
