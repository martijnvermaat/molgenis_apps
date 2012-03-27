package org.molgenis.mutation.ui.html;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.mutation.dto.ExonDTO;
import org.molgenis.mutation.dto.ProteinDomainDTO;

/*
 * A panel that prints clickable exon-intron boxes
 */
public class GenePanel extends HtmlInput implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID                = 600688454568632400L;

	private final double SCALE_FACTOR                         = 0.003; //0.1;
	private List<ProteinDomainDTO> proteinDomainSummaryVOList = new ArrayList<ProteinDomainDTO>();
	private String baseUrl                                    = "";
	private Boolean showNames                                 = true;

	public void setShowNames(Boolean showNames)
	{
		this.showNames = showNames;
	}

	@Override
	public String toHtml()
	{
		StrBuilder result = new StrBuilder();

		result.appendln("<div class=\"scrollable\">");
		result.appendln("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"1%\">");

		// first row: names
		if (this.showNames)
		{
			result.appendln("<tr>");
			for (ProteinDomainDTO proteinDomainSummaryVO : proteinDomainSummaryVOList)
			{
				List<ExonDTO> exonDTOs = proteinDomainSummaryVO.getExonDTOList();
	
				if (exonDTOs.size() > 0)
				{
					result.appendln("<td align=\"center\" valign=\"bottom\" colspan=\"" + exonDTOs.size() + "\" width=\"1%\">" + proteinDomainSummaryVO.getDomainName() + " (exon " + exonDTOs.get(0) + " - " + exonDTOs.get(exonDTOs.size() - 1) + ")</td>");
				}
			}
			result.appendln("</tr>");
		}

		result.appendln("<tr>");

		// second row: boxes
		for (ProteinDomainDTO proteinDomainSummaryVO : proteinDomainSummaryVOList)
		{
			for (ExonDTO exonDTO : proteinDomainSummaryVO.getExonDTOList())
			{
				result.append("<td align=\"left\">");
				result.append("<div class=\"pd" + proteinDomainSummaryVO.getDomainId() + "\" style=\"display: block; width: " + exonDTO.getLength() * this.SCALE_FACTOR + "px; height: 26px;\">");
				String url = this.baseUrl;
				url = StringUtils.replace(url, "domain_id=", "domain_id=" + proteinDomainSummaryVO.getDomainId());
				url = StringUtils.replace(url, "#exon", "#exon" + exonDTO.getId());
				result.append("<a style=\"display: block; height: 100%; width: 100%;\" href=\"" + url + "\" alt=\"" + exonDTO.getName() + "\" title=\"" + exonDTO.getName() + "\"></a>");
				result.append("</div>");
				result.appendln("</td>");
			}
		}

		result.appendln("</tr>");
		result.appendln("</table>");
		result.appendln("</div>");

		return result.toString();
	}

	public List<ProteinDomainDTO> getProteinDomainSummaryVOList() {
		return proteinDomainSummaryVOList;
	}

	public void setProteinDomainSummaryVOList(
			List<ProteinDomainDTO> proteinDomainSummaryVOList) {
		this.proteinDomainSummaryVOList = proteinDomainSummaryVOList;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}