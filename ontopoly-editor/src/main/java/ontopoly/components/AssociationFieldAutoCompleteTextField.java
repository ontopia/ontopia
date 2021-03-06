/*
 * #!
 * Ontopoly Editor
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */
package ontopoly.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ontopoly.model.Topic;
import ontopoly.models.RoleFieldModel;
import ontopoly.models.TopicModel;
import ontopoly.utils.TopicComparator;

import org.apache.wicket.Response;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

public abstract class AssociationFieldAutoCompleteTextField extends Panel {
  
  private static final TopicConverter TOPIC_CONVERTER = new TopicConverter();
  protected AutoCompleteTextField<Topic> textField;
  
  static final class TopicConverter implements IConverter {
    
    @Override
    public Object convertToObject(String value, Locale locale) {
      if (value != null) {
        String[] split = value.split("::");
        String topicMapId = split[0];
        String topicId = split[1];
        return new TopicModel<Topic>(topicMapId, topicId).getTopic();
      } else {
        return null;
      }
    }

    @Override
    public String convertToString(Object value, Locale locale) {
      return convertToString(value);
    }
    
    public String convertToString(Object value) {
      Topic topic = (Topic)value;
      return topic.getTopicMap().getId() + "::" + topic.getId();
    }
  }
  
  public AssociationFieldAutoCompleteTextField(String id, IModel<Topic> model, final RoleFieldModel valueFieldModel) {
    super(id);
        
    AutoCompleteSettings opts = new AutoCompleteSettings();
    opts.setCssClassName("ontopoly-autocompleter");
    opts.setAdjustInputWidth(false);
    opts.setPreselect(true);
    
    this.textField = new AutoCompleteTextField<Topic>("autoComplete", model, Topic.class, new AbstractAutoCompleteRenderer<Topic>() {
        @Override
        protected String getTextValue(Topic o) {
          return TOPIC_CONVERTER.convertToString(o);
        }
        @Override
        protected void renderChoice(Topic o, Response response, String criteria) {
          response.write(o.getName());
        }}, opts) {

      @Override
      public IConverter getConverter(Class<?> type) {
        if (Topic.class.equals(type)) {
          return new TopicConverter();
        } else {
          return super.getConverter(type);
        }
      }
    
      @Override
      protected Iterator<Topic> getChoices(String input) {
        List<Topic> result = new ArrayList<Topic>(valueFieldModel.getRoleField().searchAllowedPlayers(input));
        filterPlayers(result);
        Collections.sort(result, TopicComparator.INSTANCE);
        return result.iterator();
      }
      
      @Override
      protected void onModelChanged() {
    	super.onModelChanged();
        Topic topic = getModelObject();
        if (topic != null) {
          AssociationFieldAutoCompleteTextField.this.onTopicSelected(topic);
        }
      }
    };    
    add(textField);
  }

  protected abstract void filterPlayers(List<Topic> players);
  
  protected AutoCompleteTextField<Topic> getTextField() {
    return textField;
  }

  protected abstract void onTopicSelected(Topic topic);   
    
}
