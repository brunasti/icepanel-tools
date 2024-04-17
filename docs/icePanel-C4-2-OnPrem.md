C4Context
  title System Context diagram for null

System_Boundary(ZCIGbr1fDwSZINSjOb44, "OnPrem") {
    ContainerDb(JDgumJdxCj5dCURdCtKN, "CAPTWO", "", "")
    ContainerDb(P8FRwIacDu7PGvfS8xYm, "MQ Series", "", "")
    Component(iCsNVDctelyaR2c4wnkh, "Burst Controller", "")
    Component(gjWRDpkTJ13KPRmh36F5, "Clock", "")
    ContainerDb(oTdOuXLuTyPzLeCjIYDx, "Config DB", "", "")
    Component(FbHUk96QaZQVUumba63H, "Batch Controller", "")
    Component(ca13KkX3DwaOlAXtqCBu, "Cache Management Tool (CMT)", "")
    Component(fw0FSosDQxrXPus6KZ6i, "Control-M", "")
    Component(CBSTDIDaQZluhMUnj5Mq, "xDistributor", "")
    ContainerDb(HhxISGaz4P94iRFwLQgB, "CMT Oracle", "", "")
    Component(M6xfKV1sWabaqUXxMo6D, "Second Nightly Control-M", "")
    ContainerDb(zKtL799aVzgaZLWgIcD6, "DP", "", "")
    Component(HRtqQ8npZIhFIJyE4fny, "Control-M - Stored Procedure", "")
}


Component(p4yomm0RrOrYTNX8VvEg, "Direct Connect", "")
ContainerDb(MzZVLTUQ74KbySVwdHfZ, "OpenSearch", "", "")
Person(mtyHirRmDImFDmca6URg, "Cache Admin", "")


Rel(FbHUk96QaZQVUumba63H, p4yomm0RrOrYTNX8VvEg, "OnPrem / AWS connectivity")
BiRel(FbHUk96QaZQVUumba63H, CBSTDIDaQZluhMUnj5Mq, "adds additional info from the config database")
Rel(fw0FSosDQxrXPus6KZ6i, iCsNVDctelyaR2c4wnkh, "Every 1 minute")
Rel(JDgumJdxCj5dCURdCtKN, FbHUk96QaZQVUumba63H, "Enrich query data")
Rel(P8FRwIacDu7PGvfS8xYm, FbHUk96QaZQVUumba63H, "Message with Batch Instruction")
Rel(iCsNVDctelyaR2c4wnkh, P8FRwIacDu7PGvfS8xYm, "Puts message into MQ Series")
Rel(HhxISGaz4P94iRFwLQgB, FbHUk96QaZQVUumba63H, "Reads query data")
Rel(CBSTDIDaQZluhMUnj5Mq, oTdOuXLuTyPzLeCjIYDx, "Extract details")
Rel(gjWRDpkTJ13KPRmh36F5, HRtqQ8npZIhFIJyE4fny, "Activate")
Rel(ca13KkX3DwaOlAXtqCBu, HhxISGaz4P94iRFwLQgB, "Set search criteria")
Rel(mtyHirRmDImFDmca6URg, ca13KkX3DwaOlAXtqCBu, "Create Cache")
Rel(FbHUk96QaZQVUumba63H, MzZVLTUQ74KbySVwdHfZ, "Store Cache")
Rel(gjWRDpkTJ13KPRmh36F5, fw0FSosDQxrXPus6KZ6i, "Every 1 hour")
Rel(HRtqQ8npZIhFIJyE4fny, HhxISGaz4P94iRFwLQgB, "Invoke Stored Procedure")


  UpdateLayoutConfig($c4ShapeInRow="5", $c4BoundaryInRow="1")
