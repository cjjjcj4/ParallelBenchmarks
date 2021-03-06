package spec.benchmarks.PTcrypto.PTaes;//####[8]####
//####[8]####
import java.security.AlgorithmParameters;//####[10]####
import java.security.SecureRandom;//####[11]####
import javax.crypto.Cipher;//####[13]####
import javax.crypto.KeyGenerator;//####[14]####
import javax.crypto.SecretKey;//####[15]####
import pt.runtime.TaskID;//####[17]####
import pt.runtime.TaskIDGroup;//####[18]####
import spec.benchmarks.crypto.Util;//####[19]####
import spec.harness.Context;//####[20]####
import spec.harness.SpecJVMBenchmarkBase;//####[21]####
import spec.harness.StopBenchmarkException;//####[22]####
import spec.harness.results.BenchmarkResult;//####[23]####
//####[23]####
//-- ParaTask related imports//####[23]####
import pt.runtime.*;//####[23]####
import java.util.concurrent.ExecutionException;//####[23]####
import java.util.concurrent.locks.*;//####[23]####
import java.lang.reflect.*;//####[23]####
import pt.runtime.GuiThread;//####[23]####
import java.util.concurrent.BlockingQueue;//####[23]####
import java.util.ArrayList;//####[23]####
import java.util.List;//####[23]####
//####[23]####
public class Main extends SpecJVMBenchmarkBase {//####[25]####
    static{ParaTask.init();}//####[25]####
    /*  ParaTask helper method to access private/protected slots *///####[25]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[25]####
        if (m.getParameterTypes().length == 0)//####[25]####
            m.invoke(instance);//####[25]####
        else if ((m.getParameterTypes().length == 1))//####[25]####
            m.invoke(instance, arg);//####[25]####
        else //####[25]####
            m.invoke(instance, arg, interResult);//####[25]####
    }//####[25]####
//####[27]####
    public static final boolean DEBUG = false;//####[27]####
//####[29]####
    static final int aesKeySize = 128;//####[29]####
//####[30]####
    static final int desKeySize = 168;//####[30]####
//####[31]####
    static final int level = 12;//####[31]####
//####[33]####
    static SecretKey aesKey = null;//####[33]####
//####[34]####
    static SecretKey desKey = null;//####[34]####
//####[36]####
    static KeyGenerator aesKeyGen = null;//####[36]####
//####[37]####
    static KeyGenerator desKeyGen = null;//####[37]####
//####[39]####
    Cipher c;//####[39]####
//####[40]####
    byte[] result;//####[40]####
//####[41]####
    private static int numOfThreads = 6;//####[41]####
//####[42]####
    private static ParaTask.ScheduleType scheduleType = ParaTask.ScheduleType.WorkSharing;//####[42]####
//####[45]####
    AlgorithmParameters algorithmParameters;//####[45]####
//####[47]####
    public Main(BenchmarkResult bmResult, int threadId) {//####[47]####
        super(bmResult, threadId);//####[48]####
        algorithmParameters = null;//####[49]####
    }//####[50]####
//####[53]####
    /** Run this in multi mode, next to each other. *///####[53]####
    public static String testType() {//####[53]####
        return MULTI;//####[54]####
    }//####[55]####
//####[57]####
    private void printMe(String name, byte[] arr) {//####[57]####
        System.out.print("  " + name + ":");//####[58]####
        for (int i = 0; i < arr.length; i++) //####[59]####
        {//####[59]####
            System.out.print(arr[i]);//####[60]####
        }//####[61]####
        System.out.println();//####[62]####
    }//####[63]####
//####[73]####
    /**
     * Will encrypt the indata level number of times.
     * @param indata Data to encrypt.
     * @param key Key to use for encryption.
     * @param algorithm Algorithm/Standard to use.
     * @param level Number of times to encrypt.
     * @return The encrypted version of indata.
     *///####[73]####
    private byte[] encrypt(byte[] indata, SecretKey key, String algorithm, int level) {//####[73]####
        if (DEBUG) //####[75]####
        printMe("indata", indata);//####[75]####
        byte[] result = indata;//####[77]####
        TaskIDGroup g;//####[79]####
        g = new TaskIDGroup(numOfThreads);//####[80]####
        for (int j = 0; j < numOfThreads; j++) //####[81]####
        {//####[81]####
            TaskID<byte[]> id = doEncrypt(indata, key, algorithm, level);//####[82]####
            g.add(id);//####[83]####
            try {//####[84]####
                setResult(id.getReturnResult());//####[85]####
            } catch (Exception e) {//####[86]####
                throw new StopBenchmarkException("Exception in encrypt for " + algorithm + ".", e);//####[87]####
            }//####[88]####
        }//####[90]####
        if (DEBUG) //####[98]####
        printMe("result", result);//####[98]####
        return getResult();//####[99]####
    }//####[100]####
//####[102]####
    private void setCipher(Cipher c, int level) {//####[102]####
        this.c = c;//####[103]####
    }//####[104]####
//####[106]####
    private Cipher getCipher() {//####[106]####
        return c;//####[107]####
    }//####[108]####
//####[110]####
    private void setResult(byte[] result) {//####[110]####
        this.result = result;//####[111]####
    }//####[112]####
//####[114]####
    private byte[] getResult() {//####[114]####
        return this.result;//####[115]####
    }//####[116]####
//####[119]####
    private static volatile Method __pt__doEncrypt_byteAr_SecretKey_String_int_method = null;//####[119]####
    private synchronized static void __pt__doEncrypt_byteAr_SecretKey_String_int_ensureMethodVarSet() {//####[119]####
        if (__pt__doEncrypt_byteAr_SecretKey_String_int_method == null) {//####[119]####
            try {//####[119]####
                __pt__doEncrypt_byteAr_SecretKey_String_int_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__doEncrypt", new Class[] {//####[119]####
                    byte[].class, SecretKey.class, String.class, int.class//####[119]####
                });//####[119]####
            } catch (Exception e) {//####[119]####
                e.printStackTrace();//####[119]####
            }//####[119]####
        }//####[119]####
    }//####[119]####
    private TaskID<byte[]> doEncrypt(Object indata, Object key, Object algorithm, Object level) {//####[119]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[119]####
        return doEncrypt(indata, key, algorithm, level, new TaskInfo());//####[119]####
    }//####[119]####
    private TaskID<byte[]> doEncrypt(Object indata, Object key, Object algorithm, Object level, TaskInfo taskinfo) {//####[119]####
        // ensure Method variable is set//####[119]####
        if (__pt__doEncrypt_byteAr_SecretKey_String_int_method == null) {//####[119]####
            __pt__doEncrypt_byteAr_SecretKey_String_int_ensureMethodVarSet();//####[119]####
        }//####[119]####
        List<Integer> __pt__taskIdIndexList = new ArrayList<Integer>();//####[119]####
        List<Integer> __pt__queueIndexList = new ArrayList<Integer>();//####[119]####
        if (indata instanceof BlockingQueue) {//####[119]####
            __pt__queueIndexList.add(0);//####[119]####
        }//####[119]####
        if (indata instanceof TaskID) {//####[119]####
            taskinfo.addDependsOn((TaskID)indata);//####[119]####
            __pt__taskIdIndexList.add(0);//####[119]####
        }//####[119]####
        if (key instanceof BlockingQueue) {//####[119]####
            __pt__queueIndexList.add(1);//####[119]####
        }//####[119]####
        if (key instanceof TaskID) {//####[119]####
            taskinfo.addDependsOn((TaskID)key);//####[119]####
            __pt__taskIdIndexList.add(1);//####[119]####
        }//####[119]####
        if (algorithm instanceof BlockingQueue) {//####[119]####
            __pt__queueIndexList.add(2);//####[119]####
        }//####[119]####
        if (algorithm instanceof TaskID) {//####[119]####
            taskinfo.addDependsOn((TaskID)algorithm);//####[119]####
            __pt__taskIdIndexList.add(2);//####[119]####
        }//####[119]####
        if (level instanceof BlockingQueue) {//####[119]####
            __pt__queueIndexList.add(3);//####[119]####
        }//####[119]####
        if (level instanceof TaskID) {//####[119]####
            taskinfo.addDependsOn((TaskID)level);//####[119]####
            __pt__taskIdIndexList.add(3);//####[119]####
        }//####[119]####
        int[] __pt__queueIndexArray = new int[__pt__queueIndexList.size()];//####[119]####
        for (int __pt__i = 0; __pt__i < __pt__queueIndexArray.length; __pt__i++) {//####[119]####
            __pt__queueIndexArray[__pt__i] = __pt__queueIndexList.get(__pt__i);//####[119]####
        }//####[119]####
        taskinfo.setQueueArgIndexes(__pt__queueIndexArray);//####[119]####
        if (__pt__queueIndexArray.length > 0) {//####[119]####
            taskinfo.setIsPipeline(true);//####[119]####
        }//####[119]####
        int[] __pt__taskIdIndexArray = new int[__pt__taskIdIndexList.size()];//####[119]####
        for (int __pt__i = 0; __pt__i < __pt__taskIdIndexArray.length; __pt__i++) {//####[119]####
            __pt__taskIdIndexArray[__pt__i] = __pt__taskIdIndexList.get(__pt__i);//####[119]####
        }//####[119]####
        taskinfo.setTaskIdArgIndexes(__pt__taskIdIndexArray);//####[119]####
        taskinfo.setParameters(indata, key, algorithm, level);//####[119]####
        taskinfo.setMethod(__pt__doEncrypt_byteAr_SecretKey_String_int_method);//####[119]####
        taskinfo.setInstance(this);//####[119]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[119]####
    }//####[119]####
    public byte[] __pt__doEncrypt(byte[] indata, SecretKey key, String algorithm, int level) {//####[119]####
        byte[] result = indata;//####[122]####
        try {//####[132]####
            Cipher c = Cipher.getInstance(algorithm);//####[133]####
            c.init(Cipher.ENCRYPT_MODE, key);//####[134]####
            algorithmParameters = c.getParameters();//####[135]####
            for (int i = 0; i < level; i++) //####[138]####
            {//####[138]####
                byte[] r1 = c.update(result);//####[139]####
                byte[] r2 = c.doFinal();//####[140]####
                if (DEBUG) //####[142]####
                printMe("[" + i + "] r1", r1);//####[142]####
                if (DEBUG) //####[143]####
                printMe("[" + i + "] r2", r2);//####[143]####
                result = new byte[r1.length + r2.length];//####[146]####
                System.arraycopy(r1, 0, result, 0, r1.length);//####[147]####
                System.arraycopy(r2, 0, result, r1.length, r2.length);//####[148]####
            }//####[150]####
        } catch (Exception e) {//####[151]####
            throw new StopBenchmarkException("Exception in encrypt for " + algorithm + ".", e);//####[152]####
        }//####[153]####
        return result;//####[163]####
    }//####[164]####
//####[164]####
//####[193]####
    /**
     * Will decrypt the indata level number of times.
     * @param indata Data to decrypt.
     * @param key Key to use for encryption.
     * @param algorithm
     * @param level
     * @return
     *///####[193]####
    private byte[] decrypt(byte[] indata, SecretKey key, String algorithm, int level) {//####[193]####
        if (DEBUG) //####[195]####
        printMe("indata", indata);//####[195]####
        byte[] result = indata;//####[197]####
        try {//####[199]####
            Cipher c = Cipher.getInstance(algorithm);//####[200]####
            c.init(Cipher.DECRYPT_MODE, key, algorithmParameters);//####[201]####
            for (int i = 0; i < level; i++) //####[204]####
            {//####[204]####
                byte[] r1 = c.update(result);//####[205]####
                byte[] r2 = c.doFinal();//####[206]####
                if (DEBUG) //####[207]####
                printMe("[" + i + "] r1", r1);//####[207]####
                if (DEBUG) //####[208]####
                printMe("[" + i + "] r2", r2);//####[208]####
                result = new byte[r1.length + r2.length];//####[210]####
                System.arraycopy(r1, 0, result, 0, r1.length);//####[211]####
                System.arraycopy(r2, 0, result, r1.length, r2.length);//####[212]####
            }//####[213]####
        } catch (Exception e) {//####[215]####
            throw new StopBenchmarkException("Exception in encrypt for " + algorithm + ".", e);//####[216]####
        }//####[217]####
        if (DEBUG) //####[219]####
        printMe("result", result);//####[219]####
        return result;//####[220]####
    }//####[221]####
//####[223]####
    private static volatile Method __pt__doDecrypt_byteAr_SecretKey_String_int_method = null;//####[223]####
    private synchronized static void __pt__doDecrypt_byteAr_SecretKey_String_int_ensureMethodVarSet() {//####[223]####
        if (__pt__doDecrypt_byteAr_SecretKey_String_int_method == null) {//####[223]####
            try {//####[223]####
                __pt__doDecrypt_byteAr_SecretKey_String_int_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__doDecrypt", new Class[] {//####[223]####
                    byte[].class, SecretKey.class, String.class, int.class//####[223]####
                });//####[223]####
            } catch (Exception e) {//####[223]####
                e.printStackTrace();//####[223]####
            }//####[223]####
        }//####[223]####
    }//####[223]####
    private TaskID<Void> doDecrypt(Object indata, Object key, Object algorithm, Object level) {//####[223]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[223]####
        return doDecrypt(indata, key, algorithm, level, new TaskInfo());//####[223]####
    }//####[223]####
    private TaskID<Void> doDecrypt(Object indata, Object key, Object algorithm, Object level, TaskInfo taskinfo) {//####[223]####
        // ensure Method variable is set//####[223]####
        if (__pt__doDecrypt_byteAr_SecretKey_String_int_method == null) {//####[223]####
            __pt__doDecrypt_byteAr_SecretKey_String_int_ensureMethodVarSet();//####[223]####
        }//####[223]####
        List<Integer> __pt__taskIdIndexList = new ArrayList<Integer>();//####[223]####
        List<Integer> __pt__queueIndexList = new ArrayList<Integer>();//####[223]####
        if (indata instanceof BlockingQueue) {//####[223]####
            __pt__queueIndexList.add(0);//####[223]####
        }//####[223]####
        if (indata instanceof TaskID) {//####[223]####
            taskinfo.addDependsOn((TaskID)indata);//####[223]####
            __pt__taskIdIndexList.add(0);//####[223]####
        }//####[223]####
        if (key instanceof BlockingQueue) {//####[223]####
            __pt__queueIndexList.add(1);//####[223]####
        }//####[223]####
        if (key instanceof TaskID) {//####[223]####
            taskinfo.addDependsOn((TaskID)key);//####[223]####
            __pt__taskIdIndexList.add(1);//####[223]####
        }//####[223]####
        if (algorithm instanceof BlockingQueue) {//####[223]####
            __pt__queueIndexList.add(2);//####[223]####
        }//####[223]####
        if (algorithm instanceof TaskID) {//####[223]####
            taskinfo.addDependsOn((TaskID)algorithm);//####[223]####
            __pt__taskIdIndexList.add(2);//####[223]####
        }//####[223]####
        if (level instanceof BlockingQueue) {//####[223]####
            __pt__queueIndexList.add(3);//####[223]####
        }//####[223]####
        if (level instanceof TaskID) {//####[223]####
            taskinfo.addDependsOn((TaskID)level);//####[223]####
            __pt__taskIdIndexList.add(3);//####[223]####
        }//####[223]####
        int[] __pt__queueIndexArray = new int[__pt__queueIndexList.size()];//####[223]####
        for (int __pt__i = 0; __pt__i < __pt__queueIndexArray.length; __pt__i++) {//####[223]####
            __pt__queueIndexArray[__pt__i] = __pt__queueIndexList.get(__pt__i);//####[223]####
        }//####[223]####
        taskinfo.setQueueArgIndexes(__pt__queueIndexArray);//####[223]####
        if (__pt__queueIndexArray.length > 0) {//####[223]####
            taskinfo.setIsPipeline(true);//####[223]####
        }//####[223]####
        int[] __pt__taskIdIndexArray = new int[__pt__taskIdIndexList.size()];//####[223]####
        for (int __pt__i = 0; __pt__i < __pt__taskIdIndexArray.length; __pt__i++) {//####[223]####
            __pt__taskIdIndexArray[__pt__i] = __pt__taskIdIndexList.get(__pt__i);//####[223]####
        }//####[223]####
        taskinfo.setTaskIdArgIndexes(__pt__taskIdIndexArray);//####[223]####
        taskinfo.setParameters(indata, key, algorithm, level);//####[223]####
        taskinfo.setMethod(__pt__doDecrypt_byteAr_SecretKey_String_int_method);//####[223]####
        taskinfo.setInstance(this);//####[223]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[223]####
    }//####[223]####
    public void __pt__doDecrypt(byte[] indata, SecretKey key, String algorithm, int level) {//####[223]####
    }//####[226]####
//####[226]####
//####[228]####
    public void runEncryptDecrypt(SecretKey key, String algorithm, String inputFile) {//####[228]####
        byte[] indata = Util.getTestData(inputFile);//####[230]####
        byte[] cipher = encrypt(indata, key, algorithm, level);//####[231]####
        byte[] plain = decrypt(cipher, key, algorithm, level);//####[232]####
        boolean match = Util.check(indata, plain);//####[233]####
        Context.getOut().println(algorithm + ":" + " plaincheck=" + Util.checkSum(plain) + (match ? " PASS" : " FAIL"));//####[234]####
    }//####[236]####
//####[238]####
    public void harnessMain() {//####[238]####
        long start = System.currentTimeMillis();//####[239]####
        runEncryptDecrypt(Main.aesKey, "AES/CBC/NoPadding", Util.TEST_DATA_1);//####[241]####
        runEncryptDecrypt(Main.aesKey, "AES/CBC/PKCS5Padding", Util.TEST_DATA_1);//####[242]####
        runEncryptDecrypt(Main.desKey, "DESede/CBC/NoPadding", Util.TEST_DATA_1);//####[243]####
        runEncryptDecrypt(Main.desKey, "DESede/CBC/PKCS5Padding", Util.TEST_DATA_1);//####[244]####
        runEncryptDecrypt(Main.aesKey, "AES/CBC/NoPadding", Util.TEST_DATA_2);//####[245]####
        runEncryptDecrypt(Main.aesKey, "AES/CBC/PKCS5Padding", Util.TEST_DATA_2);//####[246]####
        runEncryptDecrypt(Main.desKey, "DESede/CBC/NoPadding", Util.TEST_DATA_2);//####[247]####
        runEncryptDecrypt(Main.desKey, "DESede/CBC/PKCS5Padding", Util.TEST_DATA_2);//####[248]####
        long time = System.currentTimeMillis() - start;//####[250]####
        System.out.println("");//####[251]####
        System.out.println("Parallel AES Crypto took " + (time / 1000.0) + " seconds");//####[253]####
    }//####[254]####
//####[256]####
    public static void setupBenchmark() {//####[256]####
        try {//####[257]####
            byte[] seed = { 0x4, 0x7, 0x1, 0x1 };//####[258]####
            SecureRandom random = new SecureRandom(seed);//####[259]####
            Context.getFileCache().loadFile(Util.TEST_DATA_1);//####[260]####
            Context.getFileCache().loadFile(Util.TEST_DATA_2);//####[261]####
            aesKeyGen = KeyGenerator.getInstance("AES");//####[262]####
            aesKeyGen.init(aesKeySize, random);//####[263]####
            desKeyGen = KeyGenerator.getInstance("DESede");//####[264]####
            desKeyGen.init(desKeySize, random);//####[265]####
            aesKey = aesKeyGen.generateKey();//####[266]####
            desKey = desKeyGen.generateKey();//####[267]####
        } catch (Exception e) {//####[268]####
            throw new StopBenchmarkException("Error in setup of crypto.aes." + e);//####[269]####
        }//####[270]####
    }//####[271]####
//####[273]####
    public static void main(String[] args) throws Exception {//####[273]####
        runSimple(Main.class, args);//####[277]####
    }//####[278]####
}//####[278]####
