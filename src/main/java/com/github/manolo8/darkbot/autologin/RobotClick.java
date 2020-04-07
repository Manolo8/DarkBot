package com.github.manolo8.darkbot.autologin;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class RobotClick {
    public static interface User32 extends StdCallLibrary {
        public static final User32 INSTANCE = (User32)Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

        WinDef.HWND FindWindow(String param1String1, String param1String2);

        int GetWindowRect(WinDef.HWND param1HWND, int[] param1ArrayOfint);
    }

    public static int[] getRect(String windowName) throws WindowNotFoundException, GetWindowRectException {
        int[] rect = { 0, 0, 0, 0 };
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, windowName);
        if (hwnd == null)
            throw new WindowNotFoundException("", windowName);
        int result = User32.INSTANCE.GetWindowRect(hwnd, rect);
        if (result == 0)
            throw new GetWindowRectException(windowName);
        return rect;
    }

    public static class WindowNotFoundException extends Exception {
        public WindowNotFoundException(String className, String windowName) {
            super(String.format("Window null for className: %s; windowName: %s", new Object[] { className, windowName }));
        }
    }

    public static class GetWindowRectException extends Exception {
        public GetWindowRectException(String windowName) {
            super("Window Rect not found for " + windowName);
        }
    }

    public RobotClick() throws AWTException, IOException, InterruptedException, GetWindowRectException, WindowNotFoundException {
        String user = null, password = null;
        File file = new File("login.txt");
        file.createNewFile();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            Scanner sc = new Scanner(file);
            user = sc.nextLine();
            password = sc.nextLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String windowName = "DarkBrowser";
        Thread.sleep(1000);
        int[] rect = getRect(windowName);
        System.out.printf("The corner locations for the window \"%s\" are %s", new Object[] { windowName,
                Arrays.toString(rect) });
        Robot robot = new Robot();
        robot.mouseMove(rect[0] + 928, rect[1] + 68);
        robot.mousePress(16);
        robot.mouseRelease(16);
        robot.delay(1000);
        StringSelection selection = new StringSelection(user);
        clipboard.setContents(selection, selection);
        robot.mousePress(4);
        robot.mouseRelease(4);
        robot.delay(100);
        robot.mouseMove(rect[0] + 948, rect[1] + 160);
        robot.mousePress(16);
        robot.mouseRelease(16);
        robot.delay(100);
        robot.mouseMove(rect[0] + 928, rect[1] + 90);
        robot.mousePress(16);
        robot.mouseRelease(16);
        selection = new StringSelection(password);
        clipboard.setContents(selection, selection);
        robot.mousePress(4);
        robot.mouseRelease(4);
        robot.delay(100);
        robot.mouseMove(rect[0] + 958, rect[1] + 182);
        robot.mousePress(16);
        robot.mouseRelease(16);
        robot.delay(100);
        robot.mouseMove(rect[0] + 1100, rect[1] + 68);
        robot.mousePress(16);
        robot.mouseRelease(16);
    }
}
