import bpy, sys, os
blend=sys.argv[-1] if len(sys.argv)>1 else ''
bpy.ops.wm.open_mainfile(filepath=blend)
required=['HVAC_L1_FullSystem','HVAC_L2_ServiceDetail','HVAC_L3_Diagnostic','OIL_L1_FullSystem','OIL_L2_ServiceDetail','OIL_L3_Diagnostic','TIMING_L1_FullSystem','TIMING_L2_ServiceDetail','TIMING_L3_Diagnostic']
missing=[n for n in required if bpy.data.collections.get(n) is None]
if missing: raise SystemExit('FAIL missing collections: '+','.join(missing))
all_objs=[]
for n in required: all_objs += list(bpy.data.collections[n].objects)
if len(all_objs)<80: raise SystemExit('FAIL too few release objects: '+str(len(all_objs)))
for o in all_objs:
 if o.get('scale_unit')!='meter': raise SystemExit('FAIL missing meter scale metadata: '+o.name)
 if 'verification_status' not in o: raise SystemExit('FAIL missing verification metadata: '+o.name)
for n in ['HVAC_L3_Diagnostic','OIL_L3_Diagnostic','TIMING_L3_Diagnostic']:
 if not bpy.data.collections[n].get('diagnostic_sequence'): raise SystemExit('FAIL missing diagnostic sequence: '+n)
print('STRUCTURE_GATE=PASS collections=%d objects=%d'%(len(required),len(all_objs)))
print('SCALE_METADATA_GATE=PASS meter-tagged=%d'%len(all_objs))
print('DIAGNOSTIC_GATE=PASS HVAC+OIL+TIMING workflows present')
print('RELEASE_GATE=PASS')
