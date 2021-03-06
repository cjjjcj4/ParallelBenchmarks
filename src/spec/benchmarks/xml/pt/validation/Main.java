package spec.benchmarks.xml.pt.validation;//####[7]####
//####[7]####
import java.io.File;//####[9]####
import java.io.IOException;//####[10]####
import java.util.Arrays;//####[12]####
import javax.xml.XMLConstants;//####[13]####
import javax.xml.parsers.ParserConfigurationException;//####[14]####
import javax.xml.transform.Source;//####[15]####
import javax.xml.transform.stream.StreamSource;//####[16]####
import javax.xml.validation.Schema;//####[17]####
import javax.xml.validation.SchemaFactory;//####[18]####
import javax.xml.validation.Validator;//####[19]####
import org.xml.sax.SAXException;//####[21]####
import spec.harness.Constants;//####[23]####
import spec.harness.Context;//####[24]####
import spec.harness.Launch;//####[25]####
import spec.harness.Util;//####[26]####
import spec.harness.results.BenchmarkResult;//####[27]####
import spec.io.FileCache;//####[28]####
import spec.io.FileCache.CachedFile;//####[29]####
import spec.benchmarks.xml.pt.XMLBenchmark;//####[31]####
//####[31]####
//-- ParaTask related imports//####[31]####
import pt.runtime.*;//####[31]####
import java.util.concurrent.ExecutionException;//####[31]####
import java.util.concurrent.locks.*;//####[31]####
import java.lang.reflect.*;//####[31]####
import pt.runtime.GuiThread;//####[31]####
import java.util.concurrent.BlockingQueue;//####[31]####
import java.util.ArrayList;//####[31]####
import java.util.List;//####[31]####
//####[31]####
public class Main extends XMLBenchmark {//####[33]####
    static{ParaTask.init();}//####[33]####
    /*  ParaTask helper method to access private/protected slots *///####[33]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[33]####
        if (m.getParameterTypes().length == 0)//####[33]####
            m.invoke(instance);//####[33]####
        else if ((m.getParameterTypes().length == 1))//####[33]####
            m.invoke(instance, arg);//####[33]####
        else //####[33]####
            m.invoke(instance, arg, interResult);//####[33]####
    }//####[33]####
//####[35]####
    private static final int XSD_NUMBER = 6;//####[35]####
//####[36]####
    private static FileCache.CachedFile[] allInstanceBytes;//####[36]####
//####[37]####
    private static FileCache.CachedFile[] allSchemaBytes;//####[37]####
//####[38]####
    private static Validator[][][] allValidators;//####[38]####
//####[39]####
    private static int THREADSNUM = 4;//####[39]####
//####[41]####
    private static int CHUNK_NUM = 4;//####[41]####
//####[44]####
    public static String testType() {//####[44]####
        return MULTI;//####[45]####
    }//####[46]####
//####[48]####
    private static String[] schemaNames = { "validation_input.xsd", "periodic_table.xsd", "play.xsd", "structure.xsd", "po.xsd", "personal.xsd" };//####[48]####
//####[57]####
    private static String[] instanceNames = { "validation_input.xml", "periodicxsd.xml", "much_adoxsd.xml", "structure.xml", "po.xml", "personal.xml" };//####[57]####
//####[75]####
    private static int loops[] = { 1, 5, 3, 52, 647, 419 };//####[75]####
//####[85]####
    public static void setupBenchmark() {//####[85]####
        String dirName = Util.getProperty(Constants.XML_VALIDATION_INPUT_DIR_PROP, null);//####[86]####
        try {//####[87]####
            allInstanceBytes = new FileCache.CachedFile[XSD_NUMBER];//####[88]####
            FileCache cache = Context.getFileCache();//####[89]####
            for (int i = 0; i < XSD_NUMBER; i++) //####[90]####
            {//####[90]####
                String name = getFullName(Main.class, dirName, instanceNames[i]);//####[91]####
                allInstanceBytes[i] = cache.new CachedFile(name);//####[92]####
                allInstanceBytes[i].cache();//####[93]####
            }//####[94]####
            allSchemaBytes = new FileCache.CachedFile[XSD_NUMBER];//####[95]####
            for (int i = 0; i < XSD_NUMBER; i++) //####[96]####
            {//####[96]####
                String name = getFullName(Main.class, dirName, schemaNames[i]);//####[97]####
                allSchemaBytes[i] = cache.new CachedFile(name);//####[98]####
                allSchemaBytes[i].cache();//####[99]####
            }//####[100]####
            setupValidators(dirName);//####[102]####
        } catch (IOException e) {//####[103]####
            e.printStackTrace(Context.getOut());//####[104]####
        }//####[105]####
    }//####[106]####
//####[108]####
    private static void setupValidators(String dirName) {//####[108]####
        int threads = Launch.currentNumberBmThreads;//####[109]####
        allValidators = new Validator[threads][XSD_NUMBER][];//####[110]####
        try {//####[111]####
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);//####[112]####
            sf.setErrorHandler(null);//####[113]####
            for (int i = 0; i < XSD_NUMBER; i++) //####[114]####
            {//####[114]####
                String xsdFilename = getFullName(Main.class, dirName, schemaNames[i]);//####[115]####
                File tempURI = new File(xsdFilename);//####[116]####
                Schema precompSchema;//####[117]####
                if (tempURI.isAbsolute()) //####[118]####
                {//####[118]####
                    precompSchema = sf.newSchema(new StreamSource(allSchemaBytes[i].getStream(), tempURI.toURI().toString()));//####[119]####
                } else {//####[121]####
                    precompSchema = sf.newSchema(new StreamSource(allSchemaBytes[i].getStream(), xsdFilename));//####[122]####
                }//####[123]####
                for (int j = 0; j < threads; j++) //####[124]####
                {//####[124]####
                    Validator[] validatorForLoops = new Validator[CHUNK_NUM];//####[125]####
                    for (int k = 0; k < CHUNK_NUM; k++) //####[126]####
                    {//####[126]####
                        validatorForLoops[k] = precompSchema.newValidator();//####[127]####
                    }//####[128]####
                    allValidators[j][i] = validatorForLoops;//####[129]####
                }//####[130]####
            }//####[131]####
        } catch (SAXException e) {//####[132]####
            e.printStackTrace();//####[133]####
        } catch (Exception e) {//####[134]####
            e.printStackTrace();//####[135]####
        }//####[136]####
    }//####[137]####
//####[139]####
    private Validator[][] schemaBoundValidator;//####[139]####
//####[141]####
    public Main(BenchmarkResult bmResult, int threadId) {//####[141]####
        super(bmResult, threadId);//####[142]####
        schemaBoundValidator = allValidators[threadId - 1];//####[143]####
    }//####[144]####
//####[146]####
    public void harnessMain() {//####[146]####
        try {//####[147]####
            long start = System.currentTimeMillis();//####[148]####
            executeWorkload();//####[149]####
            long time = System.currentTimeMillis() - start;//####[150]####
            System.out.println("PT Parallel xml validation has taken  " + (time / 1000.0) + " seconds.");//####[151]####
        } catch (Exception e) {//####[152]####
            e.printStackTrace(Context.getOut());//####[153]####
        }//####[154]####
    }//####[155]####
//####[157]####
    public static void main(String[] args) throws Exception {//####[157]####
        ParaTask.init();//####[158]####
        runSimple(Main.class, args);//####[159]####
    }//####[160]####
//####[162]####
    private void executeWorkload() throws ParserConfigurationException, IOException, SAXException {//####[163]####
        ParaTask.setThreadPoolSize(ParaTask.ThreadPoolType.ONEOFF, THREADSNUM);//####[166]####
        TaskIDGroup g;//####[168]####
        g = new TaskIDGroup(XSD_NUMBER);//####[169]####
        for (int i = 0; i < XSD_NUMBER; i++) //####[171]####
        {//####[171]####
            Context.getOut().println("Validating " + instanceNames[i]);//####[172]####
            TaskID id = doValidationTests(loops[i], allInstanceBytes[i], schemaBoundValidator[i]);//####[173]####
            g.add(id);//####[174]####
        }//####[175]####
        try {//####[176]####
            g.waitTillFinished();//####[177]####
        } catch (Exception e) {//####[178]####
            e.printStackTrace();//####[179]####
        }//####[180]####
    }//####[181]####
//####[183]####
    private static volatile Method __pt__doValidationTests_int_CachedFile_ValidatorAr_method = null;//####[183]####
    private synchronized static void __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet() {//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            try {//####[183]####
                __pt__doValidationTests_int_CachedFile_ValidatorAr_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__doValidationTests", new Class[] {//####[183]####
                    int.class, CachedFile.class, Validator[].class//####[183]####
                });//####[183]####
            } catch (Exception e) {//####[183]####
                e.printStackTrace();//####[183]####
            }//####[183]####
        }//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setTaskIdArgIndexes(0);//####[185]####
        taskinfo.addDependsOn(loops);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(0);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setTaskIdArgIndexes(1);//####[185]####
        taskinfo.addDependsOn(file);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[185]####
        taskinfo.addDependsOn(loops);//####[185]####
        taskinfo.addDependsOn(file);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(0);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setTaskIdArgIndexes(1);//####[185]####
        taskinfo.addDependsOn(file);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(1);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(1);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setTaskIdArgIndexes(0);//####[185]####
        taskinfo.addDependsOn(loops);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(0, 1);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setTaskIdArgIndexes(2);//####[185]####
        taskinfo.addDependsOn(schemaValidator);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[185]####
        taskinfo.addDependsOn(loops);//####[185]####
        taskinfo.addDependsOn(schemaValidator);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(0);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setTaskIdArgIndexes(2);//####[185]####
        taskinfo.addDependsOn(schemaValidator);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[185]####
        taskinfo.addDependsOn(file);//####[185]####
        taskinfo.addDependsOn(schemaValidator);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[185]####
        taskinfo.addDependsOn(loops);//####[185]####
        taskinfo.addDependsOn(file);//####[185]####
        taskinfo.addDependsOn(schemaValidator);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(0);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[185]####
        taskinfo.addDependsOn(file);//####[185]####
        taskinfo.addDependsOn(schemaValidator);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(1);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setTaskIdArgIndexes(2);//####[185]####
        taskinfo.addDependsOn(schemaValidator);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(1);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[185]####
        taskinfo.addDependsOn(loops);//####[185]####
        taskinfo.addDependsOn(schemaValidator);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(0, 1);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setTaskIdArgIndexes(2);//####[185]####
        taskinfo.addDependsOn(schemaValidator);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(2);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(2);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setTaskIdArgIndexes(0);//####[185]####
        taskinfo.addDependsOn(loops);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(0, 2);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(2);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setTaskIdArgIndexes(1);//####[185]####
        taskinfo.addDependsOn(file);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(2);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[185]####
        taskinfo.addDependsOn(loops);//####[185]####
        taskinfo.addDependsOn(file);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(0, 2);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setTaskIdArgIndexes(1);//####[185]####
        taskinfo.addDependsOn(file);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(1, 2);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(1, 2);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setTaskIdArgIndexes(0);//####[185]####
        taskinfo.addDependsOn(loops);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[185]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        // ensure Method variable is set//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[185]####
        }//####[185]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[185]####
        taskinfo.setIsPipeline(true);//####[185]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[185]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[185]####
        taskinfo.setInstance(this);//####[185]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[185]####
    }//####[185]####
    public void __pt__doValidationTests(int loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[185]####
        if (loops < THREADSNUM) //####[186]####
        {//####[187]####
            for (int i = loops - 1; i >= 0; i--) //####[188]####
            {//####[188]####
                validateSource(i, createDomSource(file), schemaValidator[i]);//####[189]####
                validateSource(i, createSaxSource(file), schemaValidator[i]);//####[190]####
            }//####[191]####
        } else {//####[192]####
            TaskIDGroup g;//####[193]####
            g = new TaskIDGroup(CHUNK_NUM);//####[194]####
            int[] loopsForThread = new int[CHUNK_NUM];//####[195]####
            for (int i = 0; i < CHUNK_NUM - 1; i++) //####[196]####
            {//####[197]####
                loopsForThread[i] = loops / CHUNK_NUM;//####[198]####
            }//####[199]####
            loopsForThread[CHUNK_NUM - 1] = loops - (CHUNK_NUM - 1) * loops / CHUNK_NUM;//####[200]####
            for (int j = 0; j < CHUNK_NUM; j++) //####[202]####
            {//####[203]####
                TaskID id = doValidationLoop(loopsForThread[j], file, schemaValidator[j]);//####[204]####
                g.add(id);//####[205]####
            }//####[206]####
            try {//####[207]####
                g.waitTillFinished();//####[208]####
            } catch (Exception e) {//####[209]####
                e.printStackTrace();//####[210]####
            }//####[211]####
        }//####[212]####
    }//####[214]####
//####[214]####
//####[216]####
    private static volatile Method __pt__doValidationLoop_int_CachedFile_Validator_method = null;//####[216]####
    private synchronized static void __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet() {//####[216]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[216]####
            try {//####[216]####
                __pt__doValidationLoop_int_CachedFile_Validator_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__doValidationLoop", new Class[] {//####[216]####
                    int.class, CachedFile.class, Validator.class//####[216]####
                });//####[216]####
            } catch (Exception e) {//####[216]####
                e.printStackTrace();//####[216]####
            }//####[216]####
        }//####[216]####
    }//####[216]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setTaskIdArgIndexes(0);//####[217]####
        taskinfo.addDependsOn(loop);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(0);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setTaskIdArgIndexes(1);//####[217]####
        taskinfo.addDependsOn(file);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[217]####
        taskinfo.addDependsOn(loop);//####[217]####
        taskinfo.addDependsOn(file);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(0);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setTaskIdArgIndexes(1);//####[217]####
        taskinfo.addDependsOn(file);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(1);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(1);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setTaskIdArgIndexes(0);//####[217]####
        taskinfo.addDependsOn(loop);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(0, 1);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setTaskIdArgIndexes(2);//####[217]####
        taskinfo.addDependsOn(schemaValidator);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[217]####
        taskinfo.addDependsOn(loop);//####[217]####
        taskinfo.addDependsOn(schemaValidator);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(0);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setTaskIdArgIndexes(2);//####[217]####
        taskinfo.addDependsOn(schemaValidator);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[217]####
        taskinfo.addDependsOn(file);//####[217]####
        taskinfo.addDependsOn(schemaValidator);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[217]####
        taskinfo.addDependsOn(loop);//####[217]####
        taskinfo.addDependsOn(file);//####[217]####
        taskinfo.addDependsOn(schemaValidator);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(0);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[217]####
        taskinfo.addDependsOn(file);//####[217]####
        taskinfo.addDependsOn(schemaValidator);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(1);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setTaskIdArgIndexes(2);//####[217]####
        taskinfo.addDependsOn(schemaValidator);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(1);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[217]####
        taskinfo.addDependsOn(loop);//####[217]####
        taskinfo.addDependsOn(schemaValidator);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(0, 1);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setTaskIdArgIndexes(2);//####[217]####
        taskinfo.addDependsOn(schemaValidator);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(2);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(2);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setTaskIdArgIndexes(0);//####[217]####
        taskinfo.addDependsOn(loop);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(0, 2);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(2);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setTaskIdArgIndexes(1);//####[217]####
        taskinfo.addDependsOn(file);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(2);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[217]####
        taskinfo.addDependsOn(loop);//####[217]####
        taskinfo.addDependsOn(file);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(0, 2);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setTaskIdArgIndexes(1);//####[217]####
        taskinfo.addDependsOn(file);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(1, 2);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(1, 2);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setTaskIdArgIndexes(0);//####[217]####
        taskinfo.addDependsOn(loop);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[217]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[217]####
    }//####[217]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        // ensure Method variable is set//####[217]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[217]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[217]####
        }//####[217]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[217]####
        taskinfo.setIsPipeline(true);//####[217]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[217]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[217]####
        taskinfo.setInstance(this);//####[217]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[217]####
    }//####[217]####
    public void __pt__doValidationLoop(int loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[217]####
        for (int i = loop - 1; i >= 0; i--) //####[218]####
        {//####[218]####
            validateSource(i, createDomSource(file), schemaValidator);//####[219]####
            validateSource(i, createSaxSource(file), schemaValidator);//####[220]####
        }//####[221]####
    }//####[222]####
//####[222]####
//####[228]####
    private void validateSource(int loop, Source source, Validator schemaValidator) {//####[228]####
        schemaValidator.reset();//####[229]####
        schemaValidator.setErrorHandler(null);//####[230]####
        try {//####[232]####
            schemaValidator.validate(source);//####[233]####
        } catch (SAXException e) {//####[240]####
            Context.getOut().print("\tas " + source.getClass().getName());//####[241]####
            Context.getOut().println(" failed. (Incorrect result)" + Arrays.toString(loops));//####[242]####
            e.printStackTrace(Context.getOut());//####[243]####
        } catch (IOException e) {//####[244]####
            Context.getOut().println("Unable to validate due to IOException.");//####[245]####
        }//####[246]####
    }//####[247]####
}//####[247]####
