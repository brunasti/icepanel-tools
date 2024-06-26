C4Context
  title System Context diagram for root
  Enterprise_Boundary(CtFAXDbQDsxT6tMXSJn6, "root")
    {
      ContainerDb(JDgumJdxCj5dCURdCtKN, "CAPTWO", "", "")
      Component(BxsQud7gKwTvBJQ0KNj2, "Commercial Analysis Export", "")
      ContainerDb(P8FRwIacDu7PGvfS8xYm, "MQ Series", "", "")
      Component(DzQ4VPgJYKxSkilz6snk, "BAHPCA_repo", "")
      Component(6uyz5GXgHFSVjrwIwRKC, "Common_Repo", "")
      Component(iCsNVDctelyaR2c4wnkh, "Burst Controller", "")
      System(9vmw8B9ZSNjTOfwkQyX1, "AWS", "")
      Component(nd7uOWs4bfaeXm73vKFa, "Solr_Repo", "")
      Component(gjWRDpkTJ13KPRmh36F5, "Clock", "")
      ContainerDb(oTdOuXLuTyPzLeCjIYDx, "Config DB", "", "")
      Component(FbHUk96QaZQVUumba63H, "Batch Controller", "")
      System(axBWvtIPYmpByC3xkoHt, "GitHub", "")
      Component(oLiyCOrWiJ4p4jQxIr1F, "OpenSearch Ingestion", "")
      Component(aik6DuJQc2KhSf4c5QZV, "CA Exporter", "")
      Person(f1fiXJziytRGVe7hFuLo, "Commercial Analyst", "")
      Component(heuHGHPQyPiOSCrreA29, "API Gateway", "")
      Component(p4yomm0RrOrYTNX8VvEg, "Direct Connect", "")
      Person(zG9XKg0FTcWTUZCp0G59, "External Consumer", "")
      Container(DqaxYHtWGYqlWeYgZkyP, "Common Lib", "", "")
      Component(ca13KkX3DwaOlAXtqCBu, "Cache Management Tool (CMT)", "")
      System(ZCIGbr1fDwSZINSjOb44, "OnPrem", "")
      System(LqXZYPgQ9UPjc7lqtp7S, "Akamai", "")
      Component(hQNUt4ZkKRpf1tU4WmD7, "Cache Deletion System", "")
      Container(Nr2rq32hBezwoAGeNT2U, "BAHPCA API Code", "", "")
      Container(Vg8nvjzPDirSBRjT8K3Q, "Solr API Code", "", "")
      Component(LOCysP2vMe5DXLAHEYdU, "OpenSearch Dashboard", "")
      Component(fw0FSosDQxrXPus6KZ6i, "Control-M", "")
      Component(Xoxj4IX90NjN8wiNvxFB, "Network Load Balancer", "")
      Container(JOCj92Vc2CEAUN8hquBI, "Spring Framework", "", "")
      Component(OmsC5XSTv64h2ua9yNDK, "Solr API", "")
      Person(oTbmuU7ncIHqPnGupIDk, "Developer", "")
      Component(CBSTDIDaQZluhMUnj5Mq, "xDistributor", "")
      ContainerDb(HhxISGaz4P94iRFwLQgB, "CMT Oracle", "", "")
      Component(MoA8eTudOD7sWkzZF9av, "Transit Gateway", "")
      System(cpxxV9fxmR1pgCAxHFdB, "BAH CNG", "")
      Component(F2hUPV68esNdYOZlEDZp, "Git Pipeline", "")
      Person(mtyHirRmDImFDmca6URg, "Cache Admin", "")
      Component(uFk6F6ZcKKvnkVRkCNgH, "FireWall", "")
      Component(M6xfKV1sWabaqUXxMo6D, "Second Nightly Control-M", "")
      Person(wSyyLfkRhknshpX3pdWJ, "Internal Consumer", "")
      Component(4teQrlbMUj5LLqxdVcPK, "BAHPCA API", "")
      ContainerDb(zKtL799aVzgaZLWgIcD6, "DP", "", "")
      ContainerDb(N5ViUhQIiCffQr67385p, "BAH CA - SQL Server", "Steve Edgerton:\n\n- BAHSQL1.bah.ba.com\n- CNG , schema dbo.\n- 100.64.64.215\n- Account:\nbahsql-prd (147441800008)\n- TCP 1433\n\nLive and Test users\n\nSystem:\n\nWindows Server 2019 ,SQL Server\n2017, Domain BAH.ba.com", "")
      Component(HRtqQ8npZIhFIJyE4fny, "Control-M - Stored Procedure", "")
      ContainerDb(MzZVLTUQ74KbySVwdHfZ, "OpenSearch", "", "")
      Component(sevE1WSEK6p75cxmStkb, "Git Actions", "")
    }


   Rel(cpxxV9fxmR1pgCAxHFdB, 9vmw8B9ZSNjTOfwkQyX1, "Deployed Within")
   Rel(JDgumJdxCj5dCURdCtKN, FbHUk96QaZQVUumba63H, "Enrich query data")
   Rel(P8FRwIacDu7PGvfS8xYm, FbHUk96QaZQVUumba63H, "Message with Batch Instruction")
   Rel(MzZVLTUQ74KbySVwdHfZ, Vg8nvjzPDirSBRjT8K3Q, "reads data")
   Rel(iCsNVDctelyaR2c4wnkh, P8FRwIacDu7PGvfS8xYm, "Puts message into MQ Series")
   Rel(HhxISGaz4P94iRFwLQgB, FbHUk96QaZQVUumba63H, "Reads query data")
   Rel(heuHGHPQyPiOSCrreA29, 4teQrlbMUj5LLqxdVcPK, "Accesses API implementations")
   Rel(CBSTDIDaQZluhMUnj5Mq, oTdOuXLuTyPzLeCjIYDx, "Extract details")
   Rel(DzQ4VPgJYKxSkilz6snk, Nr2rq32hBezwoAGeNT2U, "Deployed as")
   Rel(zG9XKg0FTcWTUZCp0G59, LqXZYPgQ9UPjc7lqtp7S, "Request Cache Data")
   Rel(gjWRDpkTJ13KPRmh36F5, HRtqQ8npZIhFIJyE4fny, "Activate")
   Rel(oTbmuU7ncIHqPnGupIDk, 6uyz5GXgHFSVjrwIwRKC, "Develops")
   Rel(LqXZYPgQ9UPjc7lqtp7S, heuHGHPQyPiOSCrreA29, "Access  secured systems")
   Rel(Vg8nvjzPDirSBRjT8K3Q, DqaxYHtWGYqlWeYgZkyP, "Uses")
   Rel(MzZVLTUQ74KbySVwdHfZ, Nr2rq32hBezwoAGeNT2U, "reads data")
   Rel(mtyHirRmDImFDmca6URg, ca13KkX3DwaOlAXtqCBu, "Create Cache")
   Rel(nd7uOWs4bfaeXm73vKFa, Vg8nvjzPDirSBRjT8K3Q, "Deployed as")
   Rel(MzZVLTUQ74KbySVwdHfZ, LOCysP2vMe5DXLAHEYdU, "Retrieve Cache")
   Rel(gjWRDpkTJ13KPRmh36F5, fw0FSosDQxrXPus6KZ6i, "Every 1 hour")
   Rel(oTbmuU7ncIHqPnGupIDk, DzQ4VPgJYKxSkilz6snk, "Develops")
   Rel(wSyyLfkRhknshpX3pdWJ, Xoxj4IX90NjN8wiNvxFB, "Request Cache Data")
   Rel(HRtqQ8npZIhFIJyE4fny, HhxISGaz4P94iRFwLQgB, "Invoke Stored Procedure")
   Rel(F2hUPV68esNdYOZlEDZp, 4teQrlbMUj5LLqxdVcPK, "Deploy")
   Rel(M6xfKV1sWabaqUXxMo6D, zKtL799aVzgaZLWgIcD6, "Read config from")
   Rel(Vg8nvjzPDirSBRjT8K3Q, JOCj92Vc2CEAUN8hquBI, "Based on")
   Rel(oTbmuU7ncIHqPnGupIDk, nd7uOWs4bfaeXm73vKFa, "Develops")
   Rel(DzQ4VPgJYKxSkilz6snk, F2hUPV68esNdYOZlEDZp, "Deployed via")
   Rel(Nr2rq32hBezwoAGeNT2U, JOCj92Vc2CEAUN8hquBI, "Based on")
   Rel(p4yomm0RrOrYTNX8VvEg, MoA8eTudOD7sWkzZF9av, "Flow")
   Rel(HhxISGaz4P94iRFwLQgB, iCsNVDctelyaR2c4wnkh, "Offers extracted")
   Rel(nd7uOWs4bfaeXm73vKFa, F2hUPV68esNdYOZlEDZp, "Deployed via")
   Rel(FbHUk96QaZQVUumba63H, p4yomm0RrOrYTNX8VvEg, "OnPrem / AWS connectivity")
   BiRel(uFk6F6ZcKKvnkVRkCNgH, N5ViUhQIiCffQr67385p, "Access SQL Server")
   BiRel(FbHUk96QaZQVUumba63H, CBSTDIDaQZluhMUnj5Mq, "adds additional info from the config database")
   Rel(fw0FSosDQxrXPus6KZ6i, iCsNVDctelyaR2c4wnkh, "Every 1 minute")
   Rel(BxsQud7gKwTvBJQ0KNj2, uFk6F6ZcKKvnkVRkCNgH, "Create Commercial Analysis Data")
   Rel(aik6DuJQc2KhSf4c5QZV, F2hUPV68esNdYOZlEDZp, "Deployed via")
   Rel(oLiyCOrWiJ4p4jQxIr1F, MzZVLTUQ74KbySVwdHfZ, "Store Offers")
   Rel(heuHGHPQyPiOSCrreA29, Vg8nvjzPDirSBRjT8K3Q, "Accesses API implementations")
   Rel(M6xfKV1sWabaqUXxMo6D, HhxISGaz4P94iRFwLQgB, "Nousekeeps")
   Rel(uFk6F6ZcKKvnkVRkCNgH, f1fiXJziytRGVe7hFuLo, "Analyse Commercial Data")
   Rel(LOCysP2vMe5DXLAHEYdU, mtyHirRmDImFDmca6URg, "Visualize Cache")
   Rel(ca13KkX3DwaOlAXtqCBu, HhxISGaz4P94iRFwLQgB, "Set search criteria")
   Rel(6uyz5GXgHFSVjrwIwRKC, DqaxYHtWGYqlWeYgZkyP, "Deployed as")
   Rel(F2hUPV68esNdYOZlEDZp, OmsC5XSTv64h2ua9yNDK, "Deploy")
   Rel(oTbmuU7ncIHqPnGupIDk, aik6DuJQc2KhSf4c5QZV, "Develops")
   Rel(MzZVLTUQ74KbySVwdHfZ, BxsQud7gKwTvBJQ0KNj2, "Retrieve Cache")
   Rel(F2hUPV68esNdYOZlEDZp, sevE1WSEK6p75cxmStkb, "Executes")
   Rel(MoA8eTudOD7sWkzZF9av, oLiyCOrWiJ4p4jQxIr1F, "Flow")
   Rel(F2hUPV68esNdYOZlEDZp, BxsQud7gKwTvBJQ0KNj2, "Deploy")
   Rel(hQNUt4ZkKRpf1tU4WmD7, MzZVLTUQ74KbySVwdHfZ, "Delete Cache Records")

  UpdateLayoutConfig($c4ShapeInRow="5", $c4BoundaryInRow="1")
