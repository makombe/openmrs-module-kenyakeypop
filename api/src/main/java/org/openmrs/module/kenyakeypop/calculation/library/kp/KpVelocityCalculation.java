/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyakeypop.calculation.library.kp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyakeypop.metadata.KpMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.Calendar;

public class KpVelocityCalculation extends BaseEmrCalculation {
	
	protected static final Log log = LogFactory.getLog(KpVelocityCalculation.class);
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	        PatientCalculationContext context) {
		
		CalculationResultMap ret = new CalculationResultMap();
		StringBuilder sb = new StringBuilder();
		Program kpProgram = MetadataUtils.existing(Program.class, KpMetadata._Program.KEY_POPULATION);
		
		for (Integer ptId : cohort) {
			PersonAttribute kpAlias = null;
			PersonAttributeType pt = Context.getPersonService().getPersonAttributeTypeByUuid(
			    KpMetadata._PersonAttributeType.KP_CLIENT_ALIAS);
			PersonService personService = Context.getPersonService();
			PatientService patientService = Context.getPatientService();
			kpAlias = personService.getPerson(ptId).getAttribute(pt.getId());
			
			ProgramWorkflowService service = Context.getProgramWorkflowService();
			List<PatientProgram> programs = service.getPatientPrograms(Context.getPatientService().getPatient(ptId),
			    kpProgram, null, null, null, null, true);
			
			sb.append("kpAlias:").append(kpAlias).append(",");
			if (programs.size() > 0) {
				PatientIdentifierType pit = MetadataUtils.existing(PatientIdentifierType.class,
				    KpMetadata._PatientIdentifierType.KP_UNIQUE_PATIENT_NUMBER);
				PatientIdentifier pObject = patientService.getPatient(ptId).getPatientIdentifier(pit);
				sb.append("idintifier:").append(pObject.getIdentifier()).append(",");
				
			}
			
			ret.put(ptId, new SimpleResult(sb.toString(), this, context));
		}
		return ret;
	}
	
}
