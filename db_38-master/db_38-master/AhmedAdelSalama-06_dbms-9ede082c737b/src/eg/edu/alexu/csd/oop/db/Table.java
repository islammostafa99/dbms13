package eg.edu.alexu.csd.oop.db;
//import com.sun.java.util.jar.pack.Package;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
public class Table {
    private String name;
    private String databaseName;
    private String columns;

    public Table(String databaseName, String name, String columns) {
        this.name = name;
        this.databaseName = databaseName;
        this.columns = columns;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public int insertInto(String columnsNames, String values) throws FileNotFoundException, SQLException {
        String path = databaseName + "\\" + name + ".xml";
        File file = new File(path);
        if(file.exists()) {
            File XSDFile = new File(databaseName + "\\" + name + ".xsd");
            int count = 0;
            String[] columnsNamesArray = columnsNames.split("[( ,)]+");
            String[] valuesArray = values.split("[( ,)]+");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilderFactory XSDFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(file);
                Document XSDDoc = XSDFactory.newDocumentBuilder().parse(XSDFile);
                Element element = document.createElement("row");
//                  creating columns
                NodeList list = XSDDoc.getElementsByTagName("xs:element");
                Node node;
                for(int i = 1 ; i < list.getLength() ; i++){
                    node = list.item(i);
                    boolean found = false;
                    String temp=node.getAttributes().item(0).toString();
                    temp = temp.substring(temp.indexOf("\"")+1,temp.lastIndexOf("\""));
                    for(int j = 0 ; j < columnsNamesArray.length ; j++){
                        if(temp.equals(columnsNamesArray[j])){
                            found=true;
                            Element subElement = document.createElement(temp);
                            Text value = document.createTextNode(valuesArray[j]);
                            subElement.appendChild(value);
                            element.appendChild(subElement);
                            break;
                        }
                    }
                    if(!found){
                        Element subElement = document.createElement(temp);
                        Text value = document.createTextNode(null);
                        subElement.appendChild(value);
                        element.appendChild(subElement);
                    }
                    count++;
                }
                Element root = document.getDocumentElement();
                root.appendChild(element);
//                document.appendChild(root);
//            writing from document(temp) to file
                DOMSource source = new DOMSource(document);
                Result result = new StreamResult(file.getPath());
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(source, result);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return count;
        }else{
            throw new FileNotFoundException();
        }
    }
    public int deleteFromTable(String node_name, String search, String filename) {
        int count=0;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            if(node_name==null&&search==null){
                createTable(false);
            }
            else{
                File file = new File(databaseName + "\\" + filename + ".xml");
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(file.getPath());
                NodeList lst=document.getElementsByTagName(node_name);
                for(int i=1;i<lst.getLength();i++){
                    String content=lst.item(i).getTextContent();
                    if(content.contentEquals(search)){
                        Node p=lst.item(i).getParentNode();
                        document.getDocumentElement().removeChild(p);
                    }
                }
                DOMSource source = new DOMSource(document);
                Result result = new StreamResult(file.getPath());
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(source, result);}
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        catch (TransformerException e) {
            e.printStackTrace();
        }
        catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }
    public void createTable(boolean dropIfExists) {
        String[] column = columns.split("[( ,)]+");
        if (dropIfExists) {
            File file = new File(databaseName + "\\" + name + ".xml");
            file.delete();
        }
//        writing xml file using DOM parser
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement(name);

            document.appendChild(root);

//            writing from document(temp) to file
            DOMSource source = new DOMSource(document);
            File file = new File(databaseName + "\\" + name + ".xml");
            Result result = new StreamResult(file);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);

            /*
            * XSD FILE
            * */
            document = builder.newDocument();
            Element xsSchema = document.createElement("xs:schema");
            xsSchema.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
            Element xsElement = document.createElement("xs:element");
            xsElement.setAttribute("name", "row");
            Element xsComplexType = document.createElement("xs:complexType");
            Element xsSequence = document.createElement("xs:sequence");
            for (int i = 1; i < column.length; i++) {
                Element xsCell = document.createElement("xs:element");
                xsCell.setAttribute("name", column[i]);
                i++;
                xsCell.setAttribute("type", column[i]);
                xsSequence.appendChild(xsCell);
            }
            xsComplexType.appendChild(xsSequence);
            xsElement.appendChild(xsComplexType);
            xsSchema.appendChild(xsElement);
            document.appendChild(xsSchema);
            source = new DOMSource(document);
            file = new File(databaseName + "\\" + name + ".xsd");
            result = new StreamResult(file);
            transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);

//            SchemaFactory schemaFactory =
//                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//            Schema schema = schemaFactory.newSchema(new File(databaseName+"\\"+name+".xsd"));
//            Validator validator = schema.newValidator();
//            validator.validate(new StreamSource(new File(databaseName+"\\"+name+".xml")));


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
        }
    }
    public void dropTable(boolean dropIfExists) {
        if (dropIfExists) {
            File file = new File(databaseName + "\\" + name + ".xml");
            File fileXSD = new File(databaseName + "\\" + name + ".xsd");
            file.delete();
            fileXSD.delete();
        }
    }
    public boolean dropIfExists(String path) throws SQLException {
        File file = new File(path);
        return file.exists();
    }
}