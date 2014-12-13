package nl.knaw.dans.cmd2rdf.conversion.action.transform;

import javax.xml.transform.TransformerFactory;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.AxisInfo;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.NamespaceNode;
import net.sf.saxon.tree.iter.AxisIterator;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

public final class ExtensionFunctions {
    /**
     * Registers with Saxon 9.2+ all the extension functions
     * <p>This method must be invoked once per TransformerFactory.
     *
     * @param factory the TransformerFactory pointing to
     * Saxon 9.2+ extension function registry
     * (that is, a <tt>net.sf.saxon.Configuration</tt>).
     * This object must be an instance of
     * <tt>net.sf.saxon.TransformerFactoryImpl</tt>.
     */
    public static void registerAll(TransformerFactory factory)
        throws Exception {
        Configuration config = Configuration.newConfiguration();

        config.registerExtensionFunction(new EvaluateDefinition());

        ((TransformerFactoryImpl) factory).setConfiguration(config);
    }

    // -----------------------------------------------------------------------
    // sx:evaluate
    // -----------------------------------------------------------------------

    public static final class EvaluateDefinition
                        extends ExtensionFunctionDefinition {
        public StructuredQName getFunctionQName() {
            return new StructuredQName("sx",
                                       "java:nl.knaw.dans.saxon",
                                       "evaluate");
        }

        public int getMinimumNumberOfArguments() {
            return 2;
        }

        public int getMaximumNumberOfArguments() {
            return 3;
        }

        public SequenceType[] getArgumentTypes() {
            return new SequenceType[] { SequenceType.SINGLE_NODE, SequenceType.SINGLE_STRING, SequenceType.OPTIONAL_NODE };
        }

        public SequenceType getResultType(SequenceType[] suppliedArgTypes) {
            return SequenceType.ANY_SEQUENCE;
        }
        
        public boolean dependsOnFocus() {
           return true;
        }

        public ExtensionFunctionCall makeCallExpression() {
            return new ExtensionFunctionCall() {
                @Override
                public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                    Sequence seq = null;
                    try {
                    
                        NodeInfo    node = (NodeInfo) arguments[0].head();
                        StringValue path = (StringValue) arguments[1].head();
                        NodeInfo    ns   = node;
                        if (arguments.length==3)
                            ns = (NodeInfo) arguments[2].head();

                        Processor processor = new Processor(context.getConfiguration());
                        XPathCompiler xpc   = processor.newXPathCompiler();
                        for (AxisIterator iter = ns.iterateAxis(AxisInfo.NAMESPACE);iter.moveNext();) {
                            NamespaceNode n = (NamespaceNode)iter.current();
                            xpc.declareNamespace(n.getLocalPart(),n.getStringValue());
                        }
                        XPathExecutable xpe = xpc.compile(path.asString());
                        XPathSelector xps   = xpe.load();
                        xps.setContextItem(new XdmNode(node));
                        seq = xps.evaluate().getUnderlyingValue();
                    } catch(SaxonApiException e) {
                        System.err.println("ERR: "+e.getMessage());
                        e.printStackTrace(System.err);
                    }

                    return seq;
                }
            };
        }
    }
}
