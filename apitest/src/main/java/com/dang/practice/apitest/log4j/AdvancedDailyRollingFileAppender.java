package com.dang.practice.apitest.log4j;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class AdvancedDailyRollingFileAppender extends FileAppender {
  
    // The code assumes that the following constants are in a increasing  
    // sequence.  
    static final int TOP_OF_TROUBLE = -1;  
    static final int TOP_OF_MINUTE = 0;  
    static final int TOP_OF_HOUR = 1;  
    static final int HALF_DAY = 2;  
    static final int TOP_OF_DAY = 3;  
    static final int TOP_OF_WEEK = 4;  
    static final int TOP_OF_MONTH = 5;  
  
    /** Indicates if log files should be moved to archive file */  
    private String compressBackups = "false";  
    /** Indicates if archive file that may be created will be rolled off as it ages */  
    private String rollCompressedBackups = "false";  
    /** Maximum number of days to keep log files */  
    private int maxNumberOfDays = 31;  
    /** Number of days to wait before moving a log file to an archive */  
    private int compressBackupsAfterDays = 31;  
    /** Pattern used to name archive file (also controls what log files are grouped together */  
    private String compressBackupsDatePattern = "'.'yyyy-MM";  
    /** Maximum number of days to keep archive file before deleting */  
    private int compressMaxNumberDays = 365;  
  
    /** 
     * The date pattern. By default, the pattern is set to "'.'yyyy-MM-dd" meaning daily rollover. 
     */  
    private String datePattern = "'.'yyyy-MM-dd";  
  
    /** 
     * The log file will be renamed to the value of the scheduledFilename variable when the next interval is entered. 
     * For example, if the rollover period is one hour, the log file will be renamed to the value of "scheduledFilename" 
     * at the beginning of the next hour. The precise time when a rollover occurs depends on logging activity. 
     */  
    private String scheduledFilename;  
  
    /** 
     * The next time we estimate a rollover should occur. 
     */  
    private long nextCheck = System.currentTimeMillis() - 1;  
  
    Date now = new Date();  
  
    SimpleDateFormat sdf;
  
    RollingCalendar rc = new RollingCalendar();  
  
    int checkPeriod = TOP_OF_TROUBLE;  
  
    // The gmtTimeZone is used only in computeCheckPeriod() method.  
    static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
  
    /** 
     * The default constructor does nothing. 
     */  
    public AdvancedDailyRollingFileAppender() {  
    }  
  
    /** 
     * Instantiate a <code>AdvancedDailyRollingFileAppender</code> and open the file designated by 
     * <code>filename</code>. The opened filename will become the ouput destination for this appender. 
     */  
    public AdvancedDailyRollingFileAppender(Layout layout, String filename, String datePattern) throws IOException {
        super(layout, filename, true);  
        this.datePattern = datePattern;  
        activateOptions();  
    }  
  
    /** 
     * The <b>DatePattern</b> takes a string in the same format as expected by {@link SimpleDateFormat}. This options 
     * determines the rollover schedule. 
     */  
    public void setDatePattern(String pattern) {  
        datePattern = pattern;  
    }  
  
    /** Returns the value of the <b>DatePattern</b> option. */  
    public String getDatePattern() {  
        return datePattern;  
    }  
  
    public String getCompressBackups() {  
        return compressBackups;  
    }  
  
    public void setCompressBackups(String compressBackups) {  
        this.compressBackups = compressBackups;  
    }  
  
    public String getMaxNumberOfDays() {  
        return "" + maxNumberOfDays;  
    }  
  
    public void setMaxNumberOfDays(String days) {  
        try {  
            this.maxNumberOfDays = Integer.parseInt(days);  
        } catch (Exception e) {  
            // just leave it at default  
        }  
  
    }  
  
    @Override  
    public void activateOptions() {  
        super.activateOptions();  
        if (datePattern != null && fileName != null) {  
            now.setTime(System.currentTimeMillis());  
            sdf = new SimpleDateFormat(datePattern);  
            int type = computeCheckPeriod();  
            printPeriodicity(type);  
            rc.setType(type);  
            File file = new File(fileName);  
            scheduledFilename = fileName + sdf.format(new Date(file.lastModified()));
  
        } else {  
            LogLog.error("Either File or DatePattern options are not set for appender [" + name + "].");
        }  
    }  
  
    void printPeriodicity(int type) {  
        switch (type) {  
            case TOP_OF_MINUTE:  
                LogLog.debug("Appender [" + name + "] to be rolled every minute.");  
                break;  
            case TOP_OF_HOUR:  
                LogLog.debug("Appender [" + name + "] to be rolled on top of every hour.");  
                break;  
            case HALF_DAY:  
                LogLog.debug("Appender [" + name + "] to be rolled at midday and midnight.");  
                break;  
            case TOP_OF_DAY:  
                LogLog.debug("Appender [" + name + "] to be rolled at midnight.");  
                break;  
            case TOP_OF_WEEK:  
                LogLog.debug("Appender [" + name + "] to be rolled at start of week.");  
                break;  
            case TOP_OF_MONTH:  
                LogLog.debug("Appender [" + name + "] to be rolled at start of every month.");  
                break;  
            default:  
                LogLog.warn("Unknown periodicity for appender [" + name + "].");  
        }  
    }  
  
    // This method computes the roll over period by looping over the  
    // periods, starting with the shortest, and stopping when the r0 is  
    // different from from r1, where r0 is the epoch formatted according  
    // the datePattern (supplied by the user) and r1 is the  
    // epoch+nextMillis(i) formatted according to datePattern. All date  
    // formatting is done in GMT and not local format because the test  
    // logic is based on comparisons relative to 1970-01-01 00:00:00  
    // GMT (the epoch).  
  
    int computeCheckPeriod() {  
        RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone, Locale.getDefault());
        // set sate to 1970-01-01 00:00:00 GMT  
        Date epoch = new Date(0);  
        if (datePattern != null) {  
            for (int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++) {  
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);  
                simpleDateFormat.setTimeZone(gmtTimeZone); // do all date  
                                                           // formatting in GMT  
                String r0 = simpleDateFormat.format(epoch);  
                rollingCalendar.setType(i);  
                Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));  
                String r1 = simpleDateFormat.format(next);  
                // System.out.println("Type = "+i+", r0 = "+r0+", r1 = "+r1);  
                if (r0 != null && r1 != null && !r0.equals(r1)) {  
                    return i;  
                }  
            }  
        }  
        return TOP_OF_TROUBLE; // Deliberately head for trouble...  
    }  
  
    /** 
     * Rollover the current file to a new file. 
     */  
    void rollOver() throws IOException {  
  
        /* Compute filename, but only if datePattern is specified */  
        if (datePattern == null) {  
            errorHandler.error("Missing DatePattern option in rollOver().");  
            return;  
        }  
  
        String datedFilename = fileName + sdf.format(now);  
        // It is too early to roll over because we are still within the  
        // bounds of the current interval. Rollover will occur once the  
        // next interval is reached.  
        if (scheduledFilename.equals(datedFilename)) {  
            return;  
        }  
  
        // close current file, and rename it to datedFilename  
        this.closeFile();  
  
        File target = new File(scheduledFilename);  
        if (target.exists()) {  
            target.delete();  
        }  
  
        File file = new File(fileName);  
        boolean result = file.renameTo(target);  
        if (result) {  
            LogLog.debug(fileName + " -> " + scheduledFilename);  
        } else {  
            LogLog.error("Failed to rename [" + fileName + "] to [" + scheduledFilename + "].");  
        }  
  
        try {  
            // This will also close the file. This is OK since multiple  
            // close operations are safe.  
            this.setFile(fileName, true, this.bufferedIO, this.bufferSize);  
        } catch (IOException e) {  
            errorHandler.error("setFile(" + fileName + ", true) call failed.");  
        }  
        scheduledFilename = datedFilename;  
    }  
  
    /** 
     * This method differentiates AdvancedDailyRollingFileAppender from its super class. 
     * <p> 
     * Before actually logging, this method will check whether it is time to do a rollover. If it is, it will schedule 
     * the next rollover time and then rollover. 
     */  
    @Override  
    protected void subAppend(LoggingEvent event) {
        long n = System.currentTimeMillis();  
        if (n >= nextCheck) {  
            now.setTime(n);  
            nextCheck = rc.getNextCheckMillis(now);  
            try {  
                cleanupAndRollOver();  
            } catch (IOException ioe) {  
                if (ioe instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();  
                }  
                LogLog.error("cleanupAndRollover() failed.", ioe);  
            }  
        }  
        super.subAppend(event);  
    }  
  
    /* 
     * This method checks to see if we're exceeding the number of log backups 
     * that we are supposed to keep, and if so, 
     * deletes the offending files. It then delegates to the rollover method to 
     * rollover to a new file if required. 
     */  
    protected void cleanupAndRollOver() throws IOException {  
        File file = new File(fileName);  
        Calendar cal = Calendar.getInstance();
  
        cal.add(Calendar.DATE, -maxNumberOfDays);  
        Date cutoffDate = cal.getTime();  
  
        cal = Calendar.getInstance();  
        cal.add(Calendar.DATE, -compressBackupsAfterDays);  
        Date cutoffZip = cal.getTime();  
  
        cal = Calendar.getInstance();  
        cal.add(Calendar.DATE, -compressMaxNumberDays);  
        Date cutoffDelZip = cal.getTime();  
  
        if (file.getParentFile().exists()) {  
            File[] files = file.getParentFile().listFiles(new StartsWithFileFilter(file.getName(), false));  
            int nameLength = file.getName().length();  
            for (int i = 0; i < files.length; i++) {  
                String datePart = null;  
                try {  
                    datePart = files[i].getName().substring(nameLength);  
                    Date date = sdf.parse(datePart);  
                    // cutoffDate for deletion should be further back than  
                    // cutoff for backup  
                    if (date.before(cutoffDate)) {  
                        files[i].delete();  
                    } else if (getCompressBackups().equalsIgnoreCase("YES")  
                            || getCompressBackups().equalsIgnoreCase("TRUE")) {  
                        if (date.before(cutoffZip)) {  
                            zipAndDelete(files[i], cutoffZip);  
                        }  
                    }  
                } catch (ParseException pe) {
                    // Ignore - bad parse format, not a log file, current log  
                    // file, or bad format on log file  
                } catch (Exception e) {  
                    LogLog.warn("Failed to process file " + files[i].getName(), e);  
                }  
                try {  
                    if ((getRollCompressedBackups().equalsIgnoreCase("YES") || getRollCompressedBackups()  
                            .equalsIgnoreCase("TRUE"))  
                            && files[i].getName().endsWith(".zip")) {  
                        datePart = files[i].getName().substring(nameLength, files[i].getName().length() - 4);  
                        Date date = new SimpleDateFormat(compressBackupsDatePattern).parse(datePart);  
                        if (date.before(cutoffDelZip)) {  
                            files[i].delete();  
                        }  
                    }  
                } catch (ParseException e) {  
                    // Ignore - parse exceptions mean that format is wrong or  
                    // there are other files in this dir  
                } catch (Exception e) {  
                    LogLog.warn("Evaluating archive file for rolling failed: " + files[i].getName(), e);  
                }  
            }  
        }  
        rollOver();  
    }  
  
    /** 
     * Compresses the passed file to a .zip file, stores the .zip in the same directory as the passed file, and then 
     * deletes the original, leaving only the .zipped archive. 
     * @param file 
     */  
    private void zipAndDelete(File file, Date cutoffZip) throws IOException {  
        if (!file.getName().endsWith(".zip")) {  
            String rootLogFileName = new File(fileName).getName();  
            String datePart = file.getName().substring(rootLogFileName.length());  
            String fileRoot = file.getName().substring(0, file.getName().indexOf(datePart));  
            SimpleDateFormat sdf = new SimpleDateFormat(getCompressBackupsDatePattern());  
            String newFile = fileRoot + sdf.format(cutoffZip);  
            File zipFile = new File(file.getParent(), newFile + ".zip");  
  
            if (zipFile.exists()) {  
                addFilesToExistingZip(zipFile, new File[] { file });  
            } else {  
  
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream fos = new FileOutputStream(zipFile);
                ZipOutputStream zos = new ZipOutputStream(fos);
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zos.putNextEntry(zipEntry);  
  
                byte[] buffer = new byte[4096];  
                while (true) {  
                    int bytesRead = fis.read(buffer);  
                    if (bytesRead == -1)  
                        break;  
                    else {  
                        zos.write(buffer, 0, bytesRead);  
                    }  
                }  
                zos.closeEntry();  
                fis.close();  
                zos.close();  
            }  
            file.delete();  
        }  
    }  
  
    /** 
     * This is used to add files to a zip that already exits. 
     * @param zipFile 
     * @param files 
     * @throws IOException 
     */  
    public static void addFilesToExistingZip(File zipFile, File[] files) throws IOException {  
        // get a temp file  
        File tempFile = File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.  
        tempFile.delete();  
  
        boolean renameOk = zipFile.renameTo(tempFile);  
        if (!renameOk) {  
            throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to "  
                    + tempFile.getAbsolutePath());  
        }  
        byte[] buf = new byte[1024];  
  
        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));  
  
        ZipEntry entry = zin.getNextEntry();  
        while (entry != null) {  
            String name = entry.getName();  
            boolean notInFiles = true;  
            for (File f : files) {  
                if (f.getName().equals(name)) {  
                    notInFiles = false;  
                    break;  
                }  
            }  
            if (notInFiles) {  
                // Add ZIP entry to output stream.  
                out.putNextEntry(new ZipEntry(name));  
                // Transfer bytes from the ZIP file to the output file  
                int len;  
                while ((len = zin.read(buf)) > 0) {  
                    out.write(buf, 0, len);  
                }  
            }  
            entry = zin.getNextEntry();  
        }  
        // Close the streams  
        zin.close();  
        // Compress the files  
        for (int i = 0; i < files.length; i++) {  
            InputStream in = new FileInputStream(files[i]);  
            // Add ZIP entry to output stream.  
            out.putNextEntry(new ZipEntry(files[i].getName()));  
            // Transfer bytes from the file to the ZIP file  
            int len;  
            while ((len = in.read(buf)) > 0) {  
                out.write(buf, 0, len);  
            }  
            // Complete the entry  
            out.closeEntry();  
            in.close();  
        }  
        // Complete the ZIP file  
        out.close();  
        tempFile.delete();  
    }  
  
    public String getCompressBackupsAfterDays() {  
        return "" + compressBackupsAfterDays;  
    }  
  
    public void setCompressBackupsAfterDays(String days) {  
        try {  
            compressBackupsAfterDays = Integer.parseInt(days);  
        } catch (Exception e) {  
            // ignore - just use default  
        }  
    }  
  
    public String getCompressBackupsDatePattern() {  
        return compressBackupsDatePattern;  
    }  
  
    public void setCompressBackupsDatePattern(String pattern) {  
        compressBackupsDatePattern = pattern;  
    }  
  
    public String getCompressMaxNumberDays() {  
        return compressMaxNumberDays + "";  
    }  
  
    public void setCompressMaxNumberDays(String days) {  
        try {  
            this.compressMaxNumberDays = Integer.parseInt(days);  
        } catch (Exception e) {  
            // ignore - just use default  
        }  
    }  
  
    public String getRollCompressedBackups() {  
        return rollCompressedBackups;  
    }  
  
    public void setRollCompressedBackups(String rollCompressedBackups) {  
        this.rollCompressedBackups = rollCompressedBackups;  
    }  
  
}  
  
class StartsWithFileFilter implements FileFilter {  
    private String startsWith;  
    private boolean inclDirs = false;  
  
    /** 
     *  
     */  
    public StartsWithFileFilter(String startsWith, boolean includeDirectories) {  
        super();  
        this.startsWith = startsWith.toUpperCase();  
        inclDirs = includeDirectories;  
    }  
  
    /* 
     * (non-Javadoc) 
     * @see java.io.FileFilter#accept(java.io.File) 
     */
    public boolean accept(File pathname) {  
        if (!inclDirs && pathname.isDirectory()) {  
            return false;  
        } else  
            return pathname.getName().toUpperCase().startsWith(startsWith);  
    }  
}  
  
/** 
* RollingCalendar is a helper class to AdvancedDailyRollingFileAppender. Given a periodicity type and the current time, 
* it computes the start of the next interval. 
*/  
class RollingCalendar extends GregorianCalendar {
    private static final long serialVersionUID = -3560331770601814177L;  
  
    int type = AdvancedDailyRollingFileAppender.TOP_OF_TROUBLE;  
  
    RollingCalendar() {  
        super();  
    }  
  
    RollingCalendar(TimeZone tz, Locale locale) {  
        super(tz, locale);  
    }  
  
    void setType(int type) {  
        this.type = type;  
    }  
  
    public long getNextCheckMillis(Date now) {  
        return getNextCheckDate(now).getTime();  
    }  
  
    public Date getNextCheckDate(Date now) {  
        this.setTime(now);  
  
        switch (type) {  
            case AdvancedDailyRollingFileAppender.TOP_OF_MINUTE:  
                this.set(Calendar.SECOND, 0);  
                this.set(Calendar.MILLISECOND, 0);  
                this.add(Calendar.MINUTE, 1);  
                break;  
            case AdvancedDailyRollingFileAppender.TOP_OF_HOUR:  
                this.set(Calendar.MINUTE, 0);  
                this.set(Calendar.SECOND, 0);  
                this.set(Calendar.MILLISECOND, 0);  
                this.add(Calendar.HOUR_OF_DAY, 1);  
                break;  
            case AdvancedDailyRollingFileAppender.HALF_DAY:  
                this.set(Calendar.MINUTE, 0);  
                this.set(Calendar.SECOND, 0);  
                this.set(Calendar.MILLISECOND, 0);  
                int hour = get(Calendar.HOUR_OF_DAY);  
                if (hour < 12) {  
                    this.set(Calendar.HOUR_OF_DAY, 12);  
                } else {  
                    this.set(Calendar.HOUR_OF_DAY, 0);  
                    this.add(Calendar.DAY_OF_MONTH, 1);  
                }  
                break;  
            case AdvancedDailyRollingFileAppender.TOP_OF_DAY:  
                this.set(Calendar.HOUR_OF_DAY, 0);  
                this.set(Calendar.MINUTE, 0);  
                this.set(Calendar.SECOND, 0);  
                this.set(Calendar.MILLISECOND, 0);  
                this.add(Calendar.DATE, 1);  
                break;  
            case AdvancedDailyRollingFileAppender.TOP_OF_WEEK:  
                this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());  
                this.set(Calendar.HOUR_OF_DAY, 0);  
                this.set(Calendar.MINUTE, 0);  
                this.set(Calendar.SECOND, 0);  
                this.set(Calendar.MILLISECOND, 0);  
                this.add(Calendar.WEEK_OF_YEAR, 1);  
                break;  
            case AdvancedDailyRollingFileAppender.TOP_OF_MONTH:  
                this.set(Calendar.DATE, 1);  
                this.set(Calendar.HOUR_OF_DAY, 0);  
                this.set(Calendar.MINUTE, 0);  
                this.set(Calendar.SECOND, 0);  
                this.set(Calendar.MILLISECOND, 0);  
                this.add(Calendar.MONTH, 1);  
                break;  
            default:  
                throw new IllegalStateException("Unknown periodicity type.");  
        }  
        return getTime();  
    }  
}  