
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

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
    
    File run(File code, int language, File input, String outputFilename)
            throws Exception
    {
        if(!code.exists())
        {
            error_message.append("File does not exist.\n");
            throw new FileNotFoundException();
        }
        
        switch(language)
        {
            case 0 :    // C++
                return runCppFile(code, input, outputFilename);
            case 1 :    // Java
                return runJavaFile(code, input, outputFilename);
            case 2 :    // Python 2
                return runPython2File(code, input, outputFilename);
            case 3 :    // Python 3
                return runPython3File(code, input, outputFilename);
        }
        throw new Exception("Unexpected Error !!!\n");
    }
    
    
    
    File runCppFile(File code, File input, String outputFilename)
            throws Exception
    {
        String executableFile = "executableCPP.exe";
        
        String[] compileCommand = {"g++", "-o", executableFile, 
            "-O2", "-std=c++14", code.getAbsolutePath()};
        System.out.println(Arrays.toString(compileCommand));
        
        Process compile = executeCommand(compileCommand, null, null);
        compile.waitFor();
        
        if(compile.exitValue() != 0)
        {
            printErrorMessage(compile.getInputStream());
            printErrorMessage(compile.getErrorStream());
            throw new Exception("Compilation error !!!\nMake sure your system has "
                    + "'g++' compiler preinstalled.\n");
        }
        
        
        File output = new File(code.getParent() + separator + outputFilename);
        
        String[] runCommand;
        runCommand = new String[]{"." + separator + executableFile};
            
        System.out.println(Arrays.toString(runCommand));
        
        Process execution = executeCommand(runCommand, input, output);
        execution.waitFor();
        
        if(execution.exitValue() != 0)
        {
            printErrorMessage(execution.getInputStream());
            printErrorMessage(execution.getErrorStream());
            throw new Exception("Execution failed !!!\nRuntime Error.\n");
        }
        
        return output;
    }
    
    File runJavaFile(File code, File input, String outputFilename)
            throws Exception
    {
        String[] compileCommand = {"javac", code.getAbsolutePath()};
        
        System.out.println(Arrays.toString(compileCommand));
        
        Process compile = executeCommand(compileCommand, null, null);
        compile.waitFor();
        
        if(compile.exitValue() != 0)
        {
            printErrorMessage(compile.getInputStream());
            printErrorMessage(compile.getErrorStream());
            throw new Exception("Compilation error !!!\nMake sure your system has "
                    + "JDK compiler preinstalled.\n");
        }
        
        
        File output = new File(code.getParent() + separator + outputFilename);
        
        String className = code.getName();
        className = className.substring(0, className.lastIndexOf("."));
        String[] runCommand = {"java",  "-cp", code.getParent(), className};
        System.out.println(Arrays.toString(runCommand));
        
        Process execution = executeCommand(runCommand, input, output);
        execution.waitFor();
        
        if(execution.exitValue() != 0)
        {
            printErrorMessage(execution.getInputStream());
            printErrorMessage(execution.getErrorStream());
            throw new Exception("Execution failed !!!\nRuntime Error.\n");
        }
        
        return output;
    }
    
    File runPython2File(File code, File input, String outputFilename)
            throws Exception
    {
        File output = new File(code.getParent() + separator + outputFilename);
        
        String[] runCommand = {"python2", code.getAbsolutePath()};
        System.out.println(Arrays.toString(runCommand));
        
        Process execution = executeCommand(runCommand, input, output);
        execution.waitFor();
        
        if(execution.exitValue() != 0)
        {
            printErrorMessage(execution.getInputStream());
            printErrorMessage(execution.getErrorStream());
            error_message.append("Failed to execute using 'python2' command.\n")
                    .append("Trying again with 'python' command...\n");
            return runPythonFile(code, input, outputFilename);
        }
        
        return output;
    }
    
    File runPython3File(File code, File input, String outputFilename)
            throws Exception
    {
        File output = new File(code.getParent() + separator + outputFilename);
        
        String[] runCommand = {"python3", code.getAbsolutePath()};
        System.out.println(Arrays.toString(runCommand));
        
        Process execution = executeCommand(runCommand, input, output);
        execution.waitFor();
        
        if(execution.exitValue() != 0)
        {
            printErrorMessage(execution.getInputStream());
            printErrorMessage(execution.getErrorStream());
            error_message.append("Failed to execute using 'python3' command.\n")
                    .append("Trying again with 'python' command...\n");
            return runPythonFile(code, input, outputFilename);
        }
        
        return output;
    }
    
    File runPythonFile(File code, File input, String outputFilename)
            throws Exception
    {
        File output = new File(code.getParent() + separator + outputFilename);
        
        String[] runCommand = {"python", code.getAbsolutePath()};
        System.out.println(Arrays.toString(runCommand));
        
        Process execution = executeCommand(runCommand, input, output);
        execution.waitFor();
        
        if(execution.exitValue() != 0)
        {
            printErrorMessage(execution.getInputStream());
            printErrorMessage(execution.getErrorStream());
            throw new Exception("Execution failed !!!\nRuntime Error.\n");
        }
        
        return output;
    }
    
    Process executeCommand(String[] command, File input, File output)
            throws Exception
    {
        ProcessBuilder builder = new ProcessBuilder(command);
        if(input != null)
            builder.redirectInput(input);
        if(output != null)
            builder.redirectOutput(output);
        return builder.start();
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
