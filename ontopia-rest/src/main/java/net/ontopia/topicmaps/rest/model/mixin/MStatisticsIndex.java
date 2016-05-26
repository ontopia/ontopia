
package net.ontopia.topicmaps.rest.model.mixin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import net.ontopia.topicmaps.core.index.StatisticsIndexIF;

@JsonIgnoreProperties({"index"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility = JsonAutoDetect.Visibility.NONE)
public interface MStatisticsIndex extends StatisticsIndexIF {

}
