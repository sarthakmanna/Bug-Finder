
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sarthakmanna
 */
public class Helper extends javax.swing.JFrame
{
    final String separator = File.separator;
    final String newlineCharacter = System.lineSeparator();
    
    File lastVisitedDirectory;
    StringBuilder error_message;
    boolean outputMatches;
    File input, output1, output2;
    boolean allCompiled;
    
    long inputCompile, inputExec, inputTotal;
    long sol1Compile, sol1Exec, sol1Total;
    long sol2Compile, sol2Exec, sol2Total;
    long matchOutputsTime;
    
    void resetTimers()
    {
        inputCompile = inputExec = inputTotal = -7;
        sol1Compile = sol1Exec = sol1Total = -7;
        sol2Compile = sol2Exec = sol2Total = -7;
        matchOutputsTime = -7;
    }
    
    void matchOutputs() throws Exception
    {
        outputMatches = false;
        
        if(output1 == null || output2 == null ||
                !output1.exists() || !output2.exists())
            throw new Exception("Unexpected Error !!!\n");
        
        BufferedReader reader1 = new BufferedReader(new FileReader(output1));
        BufferedReader reader2 = new BufferedReader(new FileReader(output2));
        
        for(int i = 1; ; ++i)
        {
            String line1 = reader1.readLine(), line2 = reader2.readLine();
            
            if(line1 == null && line2 == null)
            {
                outputMatches = true;
                break;
            }
            else if(line1 == null && line2 != null)
            {
                error_message.append("Reached EOF (End-of-file) of output 1\n");
                break;
            }
            else if(line1 != null && line2 == null)
            {
                error_message.append("Reached EOF (End-of-file) of output 2\n");
                break;
            }
            else if(!line1.trim().equals(line2.trim()))
            {
                error_message.append("Mismatch in outputs at line ").append(i).append("\n");
                break;
            }
        }
        
        reader1.close();
        reader2.close();
    }
    
    File writeIntoFile(String contents, String outputFilename)
            throws Exception
    {
        File file = new File(outputFilename);
        FileWriter writer = new FileWriter(file);
        StringTokenizer tokenizer = new StringTokenizer(contents, "\n");
        while(tokenizer.hasMoreTokens())
        {
            writer.write(tokenizer.nextToken());
            writer.write(newlineCharacter);
        }
        writer.close();
        return file;
    }
    
    long tempCompile, tempExec;
    
    void compileCppFile(File code, long timeLimit) throws Exception
    {
        long startTime = System.currentTimeMillis();
        String executableFile = "executableCPP.exe";
        
        String[] compileCommand = {"g++", "-o", executableFile, 
            "-O2", "-std=c++14", code.getAbsolutePath()};
        System.out.println(Arrays.toString(compileCommand));
        
        Process compile = executeCommand(compileCommand, null, null, timeLimit);
        
        if(compile.exitValue() != 0)
        {
            printErrorMessage(compile.getInputStream());
            printErrorMessage(compile.getErrorStream());
            throw new Exception("Compilation error !!!\nMake sure your system has "
                    + "'g++' compiler preinstalled.\n");
        }
        tempCompile = System.currentTimeMillis() - startTime;
    }
    
    File runCppFile(File code, File input, String outputFilename, long timeLimit)
            throws Exception
    {
        long startTime = System.currentTimeMillis();
        File output = new File(code.getParent() + separator + outputFilename);
        String executableFile = "executableCPP.exe";
        
        String[] runCommand;
        runCommand = new String[]{"." + separator + executableFile};
            
        System.out.println(Arrays.toString(runCommand));
        
        Process execution = executeCommand(runCommand, input, output, timeLimit);
        
        if(execution.exitValue() != 0)
        {
            printErrorMessage(execution.getInputStream());
            printErrorMessage(execution.getErrorStream());
            throw new Exception("Execution failed !!!\nRuntime Error.\n");
        }
        tempExec = System.currentTimeMillis() - startTime;
        
        return output;
    }
    
    void compileJavaFile(File code, long timeLimit) throws Exception
    {
        long startTime = System.currentTimeMillis();
        String[] compileCommand = {"javac", code.getAbsolutePath()};
        
        System.out.println(Arrays.toString(compileCommand));
        
        Process compile = executeCommand(compileCommand, null, null, timeLimit);
        
        if(compile.exitValue() != 0)
        {
            printErrorMessage(compile.getInputStream());
            printErrorMessage(compile.getErrorStream());
            throw new Exception("Compilation error !!!\nMake sure your system has "
                    + "JDK compiler preinstalled.\n");
        }
        tempCompile = System.currentTimeMillis() - startTime;
    }
    
    File runJavaFile(File code, File input, String outputFilename, long timeLimit)
            throws Exception
    {
        long startTime = System.currentTimeMillis();
        File output = new File(code.getParent() + separator + outputFilename);
        
        String className = code.getName();
        className = className.substring(0, className.lastIndexOf("."));
        String[] runCommand = {"java",  "-cp", code.getParent(), className};
        System.out.println(Arrays.toString(runCommand));
        
        Process execution = executeCommand(runCommand, input, output, timeLimit);
        
        if(execution.exitValue() != 0)
        {
            printErrorMessage(execution.getInputStream());
            printErrorMessage(execution.getErrorStream());
            throw new Exception("Execution failed !!!\nRuntime Error.\n");
        }
        tempExec = System.currentTimeMillis() - startTime;
        
        return output;
    }
    
    File runPython2File(File code, File input, String outputFilename, long timeLimit)
            throws Exception
    {
        long startTime = System.currentTimeMillis();
        File output = new File(code.getParent() + separator + outputFilename);
        
        String[] runCommand = {"python2", code.getAbsolutePath()};
        System.out.println(Arrays.toString(runCommand));
        
        Process execution = executeCommand(runCommand, input, output, timeLimit);
        
        if(execution.exitValue() != 0)
        {
            printErrorMessage(execution.getInputStream());
            printErrorMessage(execution.getErrorStream());
            error_message.append("Failed to execute using 'python2' command.\n")
                    .append("Trying again with 'python' command...\n");
            return runPythonFile(code, input, outputFilename, timeLimit);
        }
        tempExec = System.currentTimeMillis() - startTime;
        
        return output;
    }
    
    File runPython3File(File code, File input, String outputFilename, long timeLimit)
            throws Exception
    {
        long startTime = System.currentTimeMillis();
        File output = new File(code.getParent() + separator + outputFilename);
        
        String[] runCommand = {"python3", code.getAbsolutePath()};
        System.out.println(Arrays.toString(runCommand));
        
        Process execution = executeCommand(runCommand, input, output, timeLimit);
        
        if(execution.exitValue() != 0)
        {
            printErrorMessage(execution.getInputStream());
            printErrorMessage(execution.getErrorStream());
            error_message.append("Failed to execute using 'python3' command.\n")
                    .append("Trying again with 'python' command...\n");
            return runPythonFile(code, input, outputFilename, timeLimit);
        }
        tempExec = System.currentTimeMillis() - startTime;
        
        return output;
    }
    
    File runPythonFile(File code, File input, String outputFilename, long timeLimit)
            throws Exception
    {
        long startTime = System.currentTimeMillis();
        File output = new File(code.getParent() + separator + outputFilename);
        
        String[] runCommand = {"python", code.getAbsolutePath()};
        System.out.println(Arrays.toString(runCommand));
        
        Process execution = executeCommand(runCommand, input, output, timeLimit);
        
        if(execution.exitValue() != 0)
        {
            printErrorMessage(execution.getInputStream());
            printErrorMessage(execution.getErrorStream());
            throw new Exception("Execution failed !!!\nRuntime Error.\n");
        }
        tempExec = System.currentTimeMillis() - startTime;
        
        return output;
    }
    
    @SuppressWarnings("empty-statement")
    Process executeCommand(String[] command, File input, File output, long timeLimit)
            throws Exception
    {
        try
        {
            TimedExecution execution = new TimedExecution(command, input, output,
                    timeLimit);
            execution.start();
            while(execution.isAlive());

            if(execution.exceptionEncountered != null)
                throw execution.exceptionEncountered;
            return execution.thisProcess;
        }
        catch (Exception e)
        {
            throw new Exception("Time Limit Exceeded !!!");
        }
    }
    
    void printErrorMessage(InputStream stream) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while((line = reader.readLine()) != null)
            error_message.append(line).append("\n");
        error_message.append("\n");
        reader.close();
    }
}

class TimedExecution extends Thread
{
    Timer timer = new Timer();
    TimerTask scheduledTask = new TimerTask()
    {
        public void run()
        {
            thisProcess.destroy();
            thisProcess.destroyForcibly();
            thisInstance.interrupt();
        }
    };
    
    TimedExecution thisInstance;
    String[] command;
    File input, output;
    Process thisProcess;
    Exception exceptionEncountered;
    
    TimedExecution(String[] com, File in, File out, long timeInMillis)
    {
        timer.schedule(scheduledTask, timeInMillis);
        command = com;  input = in;   output = out;
        thisInstance = this;
    }
    
    @Override
    public void run()
    {
        try
        {
            ProcessBuilder builder = new ProcessBuilder(command);
            if(input != null)
                builder.redirectInput(input);
            if(output != null)
                builder.redirectOutput(output);

            thisProcess = builder.start();
            thisProcess.waitFor();
        }
        catch(Exception e)
        {
            exceptionEncountered = e;
        }
        timer.cancel();
    }
}