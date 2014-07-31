/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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

package net.ontopia.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class BeanUtilsTest {

  @Test
  public void testToMap() {
    Map<String, String> map = BeanUtils.beanMap(new MyBean(), false);
    Assert.assertEquals("foo", map.get("foo"));
    Assert.assertEquals("3", map.get("bar"));
    Assert.assertNull(map.get("withParams"));
    Assert.assertEquals("[<MyBean>, <MyBean>]", map.get("list"));
  }

  @Test
  public void testToMap_Sorted() {
    Map<String, String> map = BeanUtils.beanMap(new MyBean(), true);
    Assert.assertEquals(Arrays.asList("bar", "foo", "list"), new ArrayList<String>(map.keySet()));
  }

  private class MyBean {

    public String getFoo() {
      return "foo";
    }

    public int getBar() {
      return 3;
    }

    public int getWithParams(String foo) {
      return 1;
    }

    public List<MyBean> getList() {
      return Arrays.asList(new MyBean(), new MyBean());
    }

    @Override
    public String toString() {
      return "<MyBean>";
    }
  }
}
