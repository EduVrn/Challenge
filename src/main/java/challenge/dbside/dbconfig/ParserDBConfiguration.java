package challenge.dbside.dbconfig;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import challenge.dbside.models.ini.TypeAttribute;
import challenge.dbside.models.ini.TypeEntity;
import challenge.dbside.models.ini.TypeOfAttribute;
import challenge.dbside.models.ini.TypeOfEntity;
import challenge.dbside.services.ini.MediaService;

import org.w3c.dom.Node;

@Component
public class ParserDBConfiguration {

    @Autowired
    @Qualifier("storageServiceTypeOfAttribute")
    private MediaService serviceAttr;

    String currentType = null;
    String currentTypeValue = null;

    private Map<String, TypeOfEntity> addCandidateEntities;
    private Map<String, TypeOfEntity> updateCandidateEntities;
    private Map<String, TypeOfEntity> rmCandidateEntities;

    private Map<String, TypeOfEntity> allEntities;
    private Map<String, TypeOfAttribute> allAttribute;

    private Map<String, TypeOfAttribute> rmCandiateAttributes;
    private Map<String, TypeOfAttribute> addCandiateAttributes;

    public Map<String, TypeOfAttribute> getAllAttribute() {
        return allAttribute;
    }

    public Map<String, TypeOfEntity> getAllEntities() {
        return allEntities;
    }

    public Collection<TypeOfEntity> getUpdateCandidateEntities() {
        return updateCandidateEntities.values();
    }

    public Collection<TypeOfEntity> getAddCandidateEntities() {
        return addCandidateEntities.values();
    }

    public Collection<TypeOfAttribute> getAddCandiateAttributes() {
        return addCandiateAttributes.values();
    }

    public Collection<TypeOfEntity> getRmCandidateEntities() {
        return rmCandidateEntities.values();
    }

    public Collection<TypeOfAttribute> getRmCandiateAttributes() {
        return rmCandiateAttributes.values();
    }

    public ParserDBConfiguration() {
        addCandidateEntities = new HashMap();
        addCandiateAttributes = new HashMap();

        rmCandidateEntities = new HashMap();
        rmCandiateAttributes = new HashMap();

        updateCandidateEntities = new HashMap();
        allEntities = new HashMap();
        allAttribute = new HashMap();
    }

    public void applyConfiguration(InputStream input) throws Exception {
        addCandidateEntities = new HashMap();
        addCandiateAttributes = new HashMap();

        rmCandidateEntities = new HashMap();
        rmCandiateAttributes = new HashMap();

        updateCandidateEntities = new HashMap();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setIgnoringComments(true);

        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse(input);
        doc.getDocumentElement().normalize();
        parse(doc.getDocumentElement());
    }

    public void parse(Node node) throws Exception {
        String name = node.getNodeName();
        if (name.equals("attributes")) {
            currentType = "attributes";
        } else if (name.equals("entities")) {
            currentType = "entities";
        } else if (name.equals("entity")) {
            Node nodeAttr = node.getAttributes().getNamedItem("name");
            String nameEntity = nodeAttr.getNodeValue();

            nodeAttr = node.getAttributes().getNamedItem("type");
            String nameType = nodeAttr.getNodeValue();
            currentTypeValue = nameEntity;
            nodeAttr = node.getAttributes().getNamedItem("status");
            if (nodeAttr != null) {
                String status = nodeAttr.getNodeValue();
                if (status.equals("rm")) {
                    allEntities.remove(nameEntity);
                    rmCandidateEntities.remove(nameEntity);
                } else if (status.equals("alter")) {
                    TypeOfEntity t = allEntities.get(nameEntity);
                    this.updateCandidateEntities.put(t.getNameTypeEntity(), t);
                } else {
                    throw new Exception("unknown status: " + status);
                }
            } else {
                TypeOfEntity t = new TypeOfEntity(nameEntity, TypeEntity.valueOf(nameType).getValue());
                addCandidateEntities.put(t.getNameTypeEntity(), t);
                allEntities.put(t.getNameTypeEntity(), t);
            }
        } else if (name.equals("attribute")) {
            if (currentType.equals("attributes")) {
                // attr
                Node nodeAttr = node.getAttributes().getNamedItem("name");
                String nameAttr = nodeAttr.getNodeValue();
                nodeAttr = node.getAttributes().getNamedItem("id");
                Integer id = Integer.valueOf(nodeAttr.getNodeValue());

                nodeAttr = node.getAttributes().getNamedItem("type");
                String nameType = nodeAttr.getNodeValue();

                nodeAttr = node.getAttributes().getNamedItem("status");
                if (nodeAttr != null) {
                    //rm

                    String status = nodeAttr.getNodeValue();
                    if (status.equals("rm")) {
                        TypeOfAttribute t = allAttribute.get(nameAttr);
                        rmCandiateAttributes.put(nameAttr, t);

                        for (Iterator<TypeOfEntity> itr = allEntities.values().iterator(); itr.hasNext();) {
                            TypeOfEntity c = itr.next();
                            if (c.getAttributes().remove(nameAttr)) {
                                if (addCandidateEntities.containsKey(c.getNameTypeEntity())) {
                                    addCandidateEntities.remove(c.getNameTypeEntity());
                                }

                                if (updateCandidateEntities.containsKey(c.getNameTypeEntity())) {
                                    updateCandidateEntities.get(c.getNameTypeEntity()).removeAttr(nameAttr);
                                } else {
                                    updateCandidateEntities.put(c.getNameTypeEntity(), c);
                                }
                            }
                        }
                    } else {
                        throw new Exception("unknown status: " + status);
                    }
                } else {
                    //add										
                    TypeOfAttribute t = new TypeOfAttribute(id, nameAttr, TypeAttribute.valueOf(nameType).getValue());
                    addCandiateAttributes.put(t.getName(), t);
                    allAttribute.put(t.getName(), t);
                }
            } else if (currentType.equals("entities")) {
                //add attr to entity
                Node nodeAttr = node.getAttributes().getNamedItem("ref");
                String nameRef = nodeAttr.getNodeValue();

                nodeAttr = node.getAttributes().getNamedItem("status");
                if (nodeAttr != null) {
                    String status = nodeAttr.getNodeValue();
                    if (status.equals("rm")) {
                        allEntities.get(currentTypeValue).removeAttr(nameRef);
                    } else {
                        throw new Exception("unknown status: " + status);
                    }
                } else {
                    TypeOfAttribute t = allAttribute.get(nameRef);
                    allEntities.get(currentTypeValue).add(t);
                }
            }
        }

        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node cnode = node.getChildNodes().item(i);
            if (cnode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            parse(cnode);
        }

        if (name.equals("attributes")) {
            currentType = null;
        } else if (name.equals("entities")) {
            currentType = null;
        }
    }
}
