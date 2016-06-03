
package net.ontopia.topicmaps.rest.model.mixin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapSourceIF;

@JsonAutoDetect(
		fieldVisibility = JsonAutoDetect.Visibility.NONE, 
		getterVisibility = JsonAutoDetect.Visibility.NONE, 
		isGetterVisibility = JsonAutoDetect.Visibility.NONE, 
		setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
public interface MTopicMapReference extends TopicMapReferenceIF {

	@Override
	@JsonProperty
	public String getId();

	@Override
	@JsonProperty
	public String getTitle();

	@Override
	@JsonProperty
	public TopicMapSourceIF getSource();	

	@Override
	@JsonProperty
	public boolean isOpen();

	@Override
	@JsonProperty
	public boolean isDeleted();
}
