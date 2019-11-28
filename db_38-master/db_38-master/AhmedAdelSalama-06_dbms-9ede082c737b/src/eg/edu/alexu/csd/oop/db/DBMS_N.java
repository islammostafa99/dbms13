package eg.edu.alexu.csd.oop.db;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class DBMS_N implements Database {
    private String query;
    private String databaseName;
    private String [] arr ;
    private Table table;
    private String[] command;
    String p,o;
    public  DBMS_N(String query , String databaseName){
        this.query = query;
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    @Override
    public String createDatabase(String databaseName, boolean dropIfExists) {
//        boolean correct = false;
//        try {
//            correct = executeStructureQuery(query);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        if(correct) {
            if (dropIfExists) {
                File file = new File(databaseName);
                file.delete();
            }
            File file = new File(databaseName);
            System.getProperty("databaseName.separator");
            file.mkdirs();
            return file.getPath();
//        }
//        return null;
    }
    public void dropDatabase(String databaseName, boolean dropIfExists){
//        boolean correct = false;
//        try {
//            correct = executeStructureQuery(query);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        if(correct) {
            if (dropIfExists) {
                File file = new File(databaseName);
                File[] files = file.listFiles();
                for (File f : files) {
                    f.delete();
                }
                file.delete();
            }
//        }
    }


    @Override
    public boolean executeStructureQuery(String query) throws java.sql.SQLException {
        Parser parser =new Parser();
        if(parser.testWrongQuery(query)){
            return true;
        }
        throw new SQLException();
    }

    @Override
    public Object[][] executeQuery(String query) throws SQLException {
        int pivot = 0;
        String word = "from";
        String w = " ";
        String re;
        String[] arr2;
        if (!query.toLowerCase().contains(word.toLowerCase())) {
            pivot = 1;
            w = query;
        } else {
            re = query.substring(0, query.toLowerCase().indexOf(word.toLowerCase()));
            query = query.substring(query.toLowerCase().indexOf(word.toLowerCase()), query.indexOf(";"));
            this.arr = re.split("[, ]+");
            arr2 = query.split("[ ]+");
            w = arr2[1];
        }
        File file = new File(databaseName + "\\" + w + ".xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            document = builder.parse(file);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NodeList list = document.getElementsByTagName("row"); /////////////////////////////////
        int nocol = 0;
        Node q = list.item(0);
        if (q.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) q;
            NodeList names = element.getChildNodes();
            for (int j = 0; j < names.getLength(); j++) {
                Node r = names.item(j);
                if (r.getNodeType() == Node.ELEMENT_NODE) {
                    Element name = (Element) r;
                    nocol++;
                }
            }
        }
        Object[][] arr1 = new Object[list.getLength()][nocol];
        int count = 0;
        for (int i = 0; i < list.getLength(); i++) {
            count = 0;
            Node node = list.item(i);


            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                NodeList names = element.getChildNodes();
                for (int j = 0; j < names.getLength(); j++) {
                    Node r = names.item(j);
                    if (r.getNodeType() == Node.ELEMENT_NODE && i == 0) {
                        Element name = (Element) r;
                        arr1[i][count] = name.getTagName();
                        count++;
                    } else if (r.getNodeType() == Node.ELEMENT_NODE && i != 0) {
                        Element name = (Element) r;
                        arr1[i][count] = name.getTextContent();
                        count++;
                    }
                }
            }
        }
        if (pivot == 1) {
            for (int k = 0; k < list.getLength(); k++) {
                for (int j = 0; j < nocol; j++) {
                    System.out.print(arr1[k][j]);
                }
                System.out.println();
            }
            return arr1;
        } else {
            int b = 0;
            for (int i = 0; i < this.arr.length; i++) {
                for (int j = 0; j < nocol; j++) {
                    if (arr1[0][j].equals( this.arr[i])){
                        b++;
                        break;
                    }
                }
            }
            Object[][] n = new Object[list.getLength()][b];
            int v = 0;
            for (int t = 0; t < this.arr.length; t++) {
                for (int j = 0; j < nocol; j++) {
                    if (arr1[0][j].equals(this.arr[t])) {
                        for (int k = 0; k < list.getLength(); k++) {
                            n[k][v] = arr1[k][j];
                        }
                        v++;
                    }
                }
            }
            for (int k = 0; k < list.getLength(); k++) {
                for (int j = 0; j < b; j++) {
                    System.out.print(n[k][j]);
                }
                System.out.println();
            }
            return n;
        }
    }

    @Override
    public int executeUpdateQuery(String query) throws SQLException {
        boolean correct = false;
        int res=0;
        try {
            correct = executeStructureQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(query.matches("(?i)^([ ]*insert[ ]+into[ ]+[a-z_0-9]+[ ]*[(].*[)][ ]*values[ ]*[(].*[)][ ]*)$")){
            String temp = query.substring(query.indexOf("into"));
            String[] arrTemp = temp.split("[ ]+");
            table = new Table(databaseName ,arrTemp[1],query.substring(query.indexOf("("),query.indexOf(")")));
        }else if(query.matches("(?i)^([ ]*delete[ ]+from[ ]+[a-z_0-9]+" +
                "([ ]+where[ ]+([a-z_0-9]+[ ]*=[ ]*[a-z_0-9]+[ ]*))?[ ]*)$")){

        }else if(query.matches("(?i)^([ ]*update[ ]+[a-z_0-9]+[ ]+" +
                "set[ ]+(([a-z_0-9]+[ ]*=[ ]*[a-z_0-9]+[ ]*,[ ]*)*([a-z_0-9]+[ ]*=[ ]*[a-z_0-9])+|\\*[ ]*=[ ]*[a-z_0-9]+)" +
                "([ ]+where[ ]+([a-z_0-9]+[ ]*=[ ]*[a-z_0-9]+[ ]*))?[ ]*)$")) {
                    this.command =query.split("[ ]+");
                    if(this.command[2].toLowerCase().equals("set")){
                        String tablename = this.command[1].toLowerCase() ;
                        query=query.substring(query.indexOf(this.command[2])+4);
                        try {
                            res = Update(tablename, query);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        } catch (SAXException e) {
                            e.printStackTrace();
                        } catch (TransformerException e) {
                            e.printStackTrace();
                        } catch (XPathExpressionException e) {
                            e.printStackTrace();
                        }
                    }
        }
        return res;
    }
    public boolean dropIfExists(String path) throws SQLException {
        File file = new File(path);
        return file.exists();
    }


    public int Update(String fileName,String query) throws SQLException, ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {
        int counter=0;
        String x = "where";
        String[] y;
        String[] t;
        String z,b,query1,query2;
        int pivot=0;
        File XSDFile = new File(databaseName + "\\" + fileName + ".xsd");
        if(!query.toLowerCase().contains(x.toLowerCase())){
            query=query.substring(0,query.length());
            y=query.split("[, ]+");
            for(int i = 0;i<y.length;i++){
                if(y[i].equals("=")){
                    z=y[i-1].substring(0,y[i-1].length()).toLowerCase(); //the column i want to update
                    b=y[i+1].substring(0,y[i+1].length()).toLowerCase();  //the data i want to set
                    try {
                        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder docBuilder;
                        docBuilder = docFactory.newDocumentBuilder();
                        Document doc = docBuilder.parse(databaseName + "\\" + fileName + ".xml");
                        DocumentBuilderFactory XSDFactory = DocumentBuilderFactory.newInstance();
                        Document XSDDoc = XSDFactory.newDocumentBuilder().parse(XSDFile);
                        NodeList list1 = XSDDoc.getElementsByTagName("xs:element");
                        Node node1;
                        int r, g,j;
                        for(j=1;j<doc.getElementsByTagName("row").getLength();j++) {
                            Node search = doc.getElementsByTagName("row").item(j);
                            NodeList list = search.getChildNodes();
                            for (r = 0,g = 1; r < list.getLength()&&g < list1.getLength(); r++,g++) {
                                Node node = list.item(r+1);
                                node1 = list1.item(g);
                                String temp = node1.getAttributes().item(1).toString();
                                temp = temp.substring(temp.indexOf("\"") + 1, temp.lastIndexOf("\""));
                                if (z.equals(node.getNodeName().toLowerCase())) {
                                    if (temp.equals("string") && b.contains("'")) {
                                        node.setTextContent(b.substring(b.indexOf("'") + 1, b.lastIndexOf("'")));
                                        counter++;
                                    }
                                    else if (temp.equals("int") && (!b.contains("'")) && (b.matches("[0-9]+"))) {
                                        node.setTextContent(b);
                                        counter++;
                                    }
                                }
                            }
                        }
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource(doc);
                        StreamResult result = new StreamResult(new File(databaseName + "\\" + fileName + ".xml"));
                        transformer.transform(source, result);
                    } catch (ParserConfigurationException pce) {
                        pce.printStackTrace();
                    } catch (TransformerException tfe) {
                        tfe.printStackTrace();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    } catch (SAXException sae) {
                        sae.printStackTrace();
                    }
                }
            }
        }else {
            query1=query.substring(0,query.indexOf(x));
            y=query1.split("[, ]+"); //the columns i want to update
            query2=query.substring(query.indexOf(x)+6,query.length());
            t=query2.split("[, ]+"); //the rows i selected
            for(int i = 0;i<y.length;i++) {
                if (y[i].equals("=")) {
                    z = y[i - 1].substring(0, y[i - 1].length()).toLowerCase(); //the column i want to update
                    b = y[i + 1].substring(0, y[i + 1].length()).toLowerCase();  //the data i want to set
                    try {
                        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder docBuilder;
                        docBuilder = docFactory.newDocumentBuilder();
                        Document doc = docBuilder.parse(databaseName + "\\" + fileName + ".xml");
                        DocumentBuilderFactory XSDFactory = DocumentBuilderFactory.newInstance();
                        Document XSDDoc = XSDFactory.newDocumentBuilder().parse(XSDFile);
                        NodeList list1 = XSDDoc.getElementsByTagName("xs:element");
                        Node node1;
                        Node node3;
                        int r, g,j,h,count;
                        for(j=1;j<doc.getElementsByTagName("row").getLength();j++) {
                            Node search = doc.getElementsByTagName("row").item(j);
                            NodeList list = search.getChildNodes();
                            for (r = 0,g = 1; r < list.getLength()&&g < list1.getLength(); r++,g++) {
                                pivot=0;
                                Node node = list.item(r+1);
                                node1 = list1.item(g);
                                String temp = node1.getAttributes().item(1).toString();
                                temp = temp.substring(temp.indexOf("\"") + 1, temp.lastIndexOf("\""));

                                for (count = 0,h=1; count < list.getLength()-1&&h < list1.getLength(); count++,h++) {
                                    Node node2 = list.item(count+1);
                                    node3 = list1.item(h);
                                    String temp2 = node3.getAttributes().item(1).toString();
                                    temp2 = temp2.substring(temp2.indexOf("\"") + 1, temp2.lastIndexOf("\""));
                                    for (int k = 0; k < t.length; k++) {
                                        if (t[k].equals("=")) {
                                            p = t[k - 1].substring(0, t[k - 1].length()).toLowerCase(); //the column i selected
                                            o = t[k + 1].substring(0, t[k + 1].length()).toLowerCase();  //the row i selected
                                            if (z.equals(node.getNodeName().toLowerCase()) && p.equals(node2.getNodeName().toLowerCase()) && (!node2.getTextContent().toLowerCase().equals("null"))) {
                                                if (temp2.equals("int")&&!o.contains("'") && o.matches("[0-9]+")) {
                                                    if (Integer.parseInt(o) == Integer.parseInt(node2.getTextContent().toLowerCase())) {
                                                        if (temp.equals("string") && b.contains("'") && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            //b=b.substring(b.indexOf("'") + 1, b.lastIndexOf("'"));
                                                            pivot++;
                                                        } else if (temp.equals("int") && (!b.contains("'")) && (b.matches("[0-9]+")) && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            pivot++;
                                                        }
                                                    }
                                                }
                                                else if(temp2.equals("string")&&o.contains("'")){
                                                    if (o.substring(b.indexOf("'") + 1, b.lastIndexOf("'")).equals(node2.getTextContent().toLowerCase())) {
                                                        if (temp.equals("string") && b.contains("'") && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            //b=b.substring(b.indexOf("'") + 1, b.lastIndexOf("'"));
                                                            pivot++;
                                                        } else if (temp.equals("int") && (!b.contains("'")) && (b.matches("[0-9]+")) && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            pivot++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else if(t[k].equals(">")){
                                            p = t[k - 1].substring(0, t[k - 1].length()).toLowerCase(); //the column i selected
                                            o = t[k + 1].substring(0, t[k + 1].length()).toLowerCase();  //the row i selected
                                            if (z.equals(node.getNodeName().toLowerCase()) && p.equals(node2.getNodeName().toLowerCase()) && (!node2.getTextContent().toLowerCase().equals("null"))) {
                                                if (temp2.equals("int")&&!o.contains("'") && o.matches("[0-9]+")) {
                                                    if (Integer.parseInt(o) < Integer.parseInt(node2.getTextContent().toLowerCase())) {
                                                        if (temp.equals("string") && b.contains("'") && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            //b=b.substring(b.indexOf("'") + 1, b.lastIndexOf("'"));
                                                            pivot++;
                                                        } else if (temp.equals("int") && (!b.contains("'")) && (b.matches("[0-9]+")) && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            pivot++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else if(t[k].equals("<")){
                                            p = t[k - 1].substring(0, t[k - 1].length()).toLowerCase(); //the column i selected
                                            o = t[k + 1].substring(0, t[k + 1].length()).toLowerCase();  //the row i selected
                                            if (z.equals(node.getNodeName().toLowerCase()) && p.equals(node2.getNodeName().toLowerCase()) && (!node2.getTextContent().toLowerCase().equals("null"))) {
                                                if (temp2.equals("int")&&!o.contains("'") && o.matches("[0-9]+")) {
                                                    if (Integer.parseInt(o) > Integer.parseInt(node2.getTextContent().toLowerCase())) {
                                                        if (temp.equals("string") && b.contains("'") && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            //b=b.substring(b.indexOf("'") + 1, b.lastIndexOf("'"));
                                                            pivot++;
                                                        } else if (temp.equals("int") && (!b.contains("'")) && (b.matches("[0-9]+")) && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            pivot++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else if(t[k].equals(">=")){
                                            p = t[k - 1].substring(0, t[k - 1].length()).toLowerCase(); //the column i selected
                                            o = t[k + 1].substring(0, t[k + 1].length()).toLowerCase();  //the row i selected
                                            if (z.equals(node.getNodeName().toLowerCase()) && p.equals(node2.getNodeName().toLowerCase()) && (!node2.getTextContent().toLowerCase().equals("null"))) {
                                                if (temp2.equals("int")&&!o.contains("'") && o.matches("[0-9]+")) {
                                                    if (Integer.parseInt(o) <= Integer.parseInt(node2.getTextContent().toLowerCase())) {
                                                        if (temp.equals("string") && b.contains("'") && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            //b=b.substring(b.indexOf("'") + 1, b.lastIndexOf("'"));
                                                            pivot++;
                                                        } else if (temp.equals("int") && (!b.contains("'")) && (b.matches("[0-9]+")) && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            pivot++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else if(t[k].equals("<=")){
                                            p = t[k - 1].substring(0, t[k - 1].length()).toLowerCase(); //the column i selected
                                            o = t[k + 1].substring(0, t[k + 1].length()).toLowerCase();  //the row i selected
                                            if (z.equals(node.getNodeName().toLowerCase()) && p.equals(node2.getNodeName().toLowerCase()) && (!node2.getTextContent().toLowerCase().equals("null"))) {
                                                if (temp2.equals("int")&&!o.contains("'") && o.matches("[0-9]+")) {
                                                    if (Integer.parseInt(o) >= Integer.parseInt(node2.getTextContent().toLowerCase())) {
                                                        if (temp.equals("string") && b.contains("'") && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            //b=b.substring(b.indexOf("'") + 1, b.lastIndexOf("'"));
                                                            pivot++;
                                                        } else if (temp.equals("int") && (!b.contains("'")) && (b.matches("[0-9]+")) && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            pivot++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else if(t[k].equals("!=")){
                                            p = t[k - 1].substring(0, t[k - 1].length()).toLowerCase(); //the column i selected
                                            o = t[k + 1].substring(0, t[k + 1].length()).toLowerCase();  //the row i selected
                                            if (z.equals(node.getNodeName().toLowerCase()) && p.equals(node2.getNodeName().toLowerCase()) && (!node2.getTextContent().toLowerCase().equals("null"))) {
                                                if (temp2.equals("int")&&!o.contains("'") && o.matches("[0-9]+")) {
                                                    if (Integer.parseInt(o) != Integer.parseInt(node2.getTextContent().toLowerCase())) {
                                                        if (temp.equals("string") && b.contains("'") && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            //b=b.substring(b.indexOf("'") + 1, b.lastIndexOf("'"));
                                                            pivot++;
                                                        } else if (temp.equals("int") && (!b.contains("'")) && (b.matches("[0-9]+")) && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            pivot++;
                                                        }
                                                    }
                                                }
                                                else if(temp2.equals("string")&&o.contains("'")){
                                                    if (!o.substring(b.indexOf("'") + 1, b.lastIndexOf("'")).equals(node2.getTextContent().toLowerCase())) {
                                                        if (temp.equals("string") && b.contains("'") && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            //b=b.substring(b.indexOf("'") + 1, b.lastIndexOf("'"));
                                                            pivot++;
                                                        } else if (temp.equals("int") && (!b.contains("'")) && (b.matches("[0-9]+")) && ((temp2.equals("string") && o.contains("'")) || ((temp2.equals("int") && !o.contains("'") && o.matches("[0-9]+"))))) {
                                                            pivot++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (pivot == t.length / 3) {
                                    if (temp.equals("string") && b.contains("'")) {
                                        node.setTextContent(b.substring(b.indexOf("'") + 1, b.lastIndexOf("'")));
                                        counter++;
                                    }else if (temp.equals("int") && (!b.contains("'")) && (b.matches("[0-9]+"))) {
                                        node.setTextContent(b);
                                        counter++;
                                    }
                                }
                            }
                        }
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource(doc);
                        StreamResult result = new StreamResult(new File(databaseName + "\\" + fileName + ".xml"));
                        transformer.transform(source, result);
                    } catch (ParserConfigurationException pce) {
                        pce.printStackTrace();
                    } catch (TransformerException tfe) {
                        tfe.printStackTrace();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    } catch (SAXException sae) {
                        sae.printStackTrace();
                    }
                }
            }
        }
        return counter;
    }
}