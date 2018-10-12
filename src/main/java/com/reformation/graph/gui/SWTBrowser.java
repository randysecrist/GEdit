/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.reformation.graph.gui;
/*
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
*/
/**
 * An interface to a native browser, using SWT.
 * Created on Nov 11, 2004
 * @author Randy Secrist
public class SWTBrowser {
    private String title = "SWT Browser";

    private String homeUrl = "http://www.secristfamily.com/sfn";

    private Shell sShell = null; //  @jve:decl-index=0:visual-constraint="10,10"

    private Button backButton = null;

    private Button forwardButton = null;

    private Button stopButton = null;

    private Text locationText = null;

    private Button goButton = null;

    private Browser browser = null;

    private Button homeButton = null;

    private Label statusText = null;

    private ProgressBar progressBar = null;

    private Button refreshButton = null;

    private void createBrowser() {
        GridData gridData3 = new GridData();
        browser = new Browser(sShell, SWT.BORDER);
        gridData3.horizontalSpan = 7;
        gridData3.horizontalAlignment = GridData.FILL;
        gridData3.verticalAlignment = GridData.FILL;
        gridData3.grabExcessVerticalSpace = true;
        browser.setLayoutData(gridData3);
        browser.addTitleListener(new TitleListener() {
            public void changed(TitleEvent e) {
                sShell.setText(title + " - " + e.title);
            }
        });
        browser.addLocationListener(new LocationListener() {
                    public void changing(LocationEvent e) {
                        locationText.setText(e.location);
                    }

                    public void changed(LocationEvent e) {
                    }
                });
        browser.addProgressListener(new ProgressListener() {
                    public void changed(ProgressEvent e) {
                        if (!stopButton.isEnabled() && e.total != e.current) {
                            stopButton.setEnabled(true);
                        }
                        progressBar.setMaximum(e.total);
                        progressBar.setSelection(e.current);
                    }

                    public void completed(
                            ProgressEvent e) {
                        stopButton.setEnabled(false);
                        backButton.setEnabled(browser.isBackEnabled());
                        forwardButton.setEnabled(browser.isForwardEnabled());
                        progressBar.setSelection(0);
                    }
                });
        browser.addStatusTextListener(new StatusTextListener() {
                    public void changed(
                            StatusTextEvent e) {
                        statusText.setText(e.text);
                    }
                });
        browser.setUrl(homeUrl);
    }

    public static void main(String[] args) {
        // Before this is run, be sure to set up the following in the launch configuration
        // (Arguments->VM Arguments) for the correct SWT library path.
        // The following is a windows example:
        // -Djava.library.path="installation_directory\plugins\org.eclipse.swt.win32_3.0.0\os\win32\x86"
        Display display = Display.getDefault();
        SWTBrowser thisClass = new SWTBrowser();
        thisClass.createSShell();
        thisClass.sShell.open();

        while (!thisClass.sShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    void createSShell() {
        sShell = new Shell();
        GridLayout gridLayout1 = new GridLayout();
        GridData gridData2 = new GridData();
        GridData gridData4 = new GridData();
        GridData gridData5 = new GridData();
        GridData gridData6 = new GridData();
        GridData gridData7 = new GridData();
        GridData gridData8 = new GridData();
        backButton = new Button(sShell, SWT.ARROW | SWT.LEFT);
        forwardButton = new Button(sShell, SWT.ARROW | SWT.RIGHT);
        stopButton = new Button(sShell, SWT.NONE);
        refreshButton = new Button(sShell, SWT.NONE);
        homeButton = new Button(sShell, SWT.NONE);
        locationText = new Text(sShell, SWT.BORDER);
        goButton = new Button(sShell, SWT.NONE);
        createBrowser();
        progressBar = new ProgressBar(sShell, SWT.BORDER);
        statusText = new Label(sShell, SWT.NONE);
        sShell.setText(title);
        sShell.setLayout(gridLayout1);
        gridLayout1.numColumns = 7;
        backButton.setEnabled(false);
        backButton.setToolTipText("Navigate back to the previous page");
        backButton.setLayoutData(gridData6);
        forwardButton.setEnabled(false);
        forwardButton.setToolTipText("Navigate forward to the next page");
        forwardButton.setLayoutData(gridData5);
        stopButton.setText("Stop");
        stopButton.setEnabled(false);
        stopButton.setToolTipText("Stop the loading of the current page");
        goButton.setText("Go!");
        goButton.setLayoutData(gridData8);
        goButton.setToolTipText("Navigate to the selected web address");
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.verticalAlignment = GridData.CENTER;
        locationText.setLayoutData(gridData2);
        locationText.setText(homeUrl);
        locationText.setToolTipText("Enter a web address");
        homeButton.setText("Home");
        homeButton.setToolTipText("Return to home page");
        statusText.setText("Done");
        statusText.setLayoutData(gridData7);
        gridData4.horizontalSpan = 5;
        progressBar.setLayoutData(gridData4);
        progressBar.setEnabled(false);
        progressBar.setSelection(0);
        gridData5.horizontalAlignment = GridData.FILL;
        gridData5.verticalAlignment = GridData.FILL;
        gridData6.horizontalAlignment = GridData.FILL;
        gridData6.verticalAlignment = GridData.FILL;
        gridData7.horizontalSpan = 1;
        gridData7.grabExcessHorizontalSpace = true;
        gridData7.horizontalAlignment = GridData.FILL;
        gridData7.verticalAlignment = GridData.CENTER;
        gridData8.horizontalAlignment = GridData.END;
        gridData8.verticalAlignment = GridData.CENTER;
        refreshButton.setText("Refresh");
        refreshButton.setToolTipText("Refresh the current page");
        sShell.setSize(new Point(750,500));
        locationText.addMouseListener(new MouseAdapter() {
                    public void mouseUp(MouseEvent e) {
                        locationText.selectAll();
                    }
                });
        locationText.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                // Handle the press of the Enter key in the locationText.
                // This will browse to the entered text.
                if (e.character == SWT.LF || e.character == SWT.CR) {
                    e.doit = false;
                    browser.setUrl(locationText.getText());
                }
            }
        });
        refreshButton.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(
                            SelectionEvent e) {
                        browser.refresh();
                    }
                });
        locationText.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(
                            SelectionEvent e) {
                        browser.setUrl(locationText.getText());
                    }
                });
        stopButton.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(
                            SelectionEvent e) {
                        browser.stop();
                    }
                });
        backButton.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(
                            SelectionEvent e) {
                        browser.back();
                    }
                });
        forwardButton.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(
                            SelectionEvent e) {
                        browser.forward();
                    }
                });
        homeButton.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(
                            SelectionEvent e) {
                        browser.setUrl(homeUrl);
                    }
                });
        goButton.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(
                            SelectionEvent e) {
                        browser.setUrl(locationText.getText());
                    }
                });
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void open() {
        this.sShell.open();
    }
    public boolean isDisposed() {
        return this.sShell.isDisposed();
    }
    public void dispose() {
        sShell.dispose();
    }
}
*/
