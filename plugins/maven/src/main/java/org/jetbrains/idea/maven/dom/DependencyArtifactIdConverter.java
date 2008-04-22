package org.jetbrains.idea.maven.dom;

import com.intellij.openapi.project.Project;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.dom.beans.Dependency;
import org.jetbrains.idea.maven.repository.MavenRepositoryException;
import org.jetbrains.idea.maven.repository.MavenRepositoryManager;
import org.sonatype.nexus.index.ArtifactInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class DependencyArtifactIdConverter extends ResolvingConverter<String> {
  public String fromString(@Nullable @NonNls String s, ConvertContext context) {
    return getVariants(context).contains(s) ? s : null;
  }

  public String toString(@Nullable String s, ConvertContext context) {
    return s;
  }

  @NotNull
  public Collection<String> getVariants(ConvertContext context) {
    try {
      GenericDomValue<String> g = ((Dependency)context.getInvocationElement().getParent()).getGroupId();
      Project p = context.getModule().getProject();
      TermQuery q = new TermQuery(new Term(ArtifactInfo.GROUP_ID, g.getStringValue()));
      Collection<String> result = new ArrayList<String>();
      for (ArtifactInfo each : MavenRepositoryManager.getInstance(p).search(q)) {
        result.add(each.artifactId);
      }
      return result;
    }
    catch (MavenRepositoryException e) {
      return Collections.emptyList();
    }
  }
}
