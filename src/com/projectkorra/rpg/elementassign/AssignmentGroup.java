package com.projectkorra.rpg.elementassign;

import com.projectkorra.projectkorra.Element;

import java.util.ArrayList;
import java.util.List;

public class AssignmentGroup {

     private String name;
     private List<Element> elements;
     private double weight;
     private boolean enabled;
     private String prefix;
     private String permissionGroup;

     private List<String> commandsToRun;

     public AssignmentGroup(String name, List<String> elements, double weight, boolean enabled, String prefix, List<String> commandsToRun, String permissionGroup) {
          this.name = name;
          this.weight = weight;
          this.enabled = enabled;
          this.prefix = prefix;
          this.permissionGroup = permissionGroup;
          this.commandsToRun = commandsToRun;
          this.elements = new ArrayList<>();
         for (String elementName : elements) {
              Element element = Element.getElement(elementName);
              if (element != null) {
                  this.elements.add(element);
              }
          }
     }


     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }

     public List<Element> getElements() {
          return elements;
     }

     public void addElement(Element element) {
          this.elements.add(element);
     }

   public void removeElement(Element element) {
       this.elements.remove(element);
   }

     public void setElements(List<Element> elements) {
          this.elements = elements;
     }

     public double getWeight() {
          return weight;
     }

     public void setWeight(double weight) {
          this.weight = weight;
     }

     public boolean isEnabled() {
          return enabled;
     }

     public void setEnabled(boolean enabled) {
          this.enabled = enabled;
     }

     public String getPrefix() {
          return prefix;
     }

     public void setPrefix(String prefix) {
          this.prefix = prefix;
     }

     public String getPermissionGroup() {
          return permissionGroup;
     }

     public void setPermissionGroup(String permissionGroup) {
          this.permissionGroup = permissionGroup;
     }

     public List<String> getCommandsToRun() {
          return commandsToRun;
     }

     public void setCommandsToRun(List<String> commandsToRun) {
          this.commandsToRun = commandsToRun;
     }
}
