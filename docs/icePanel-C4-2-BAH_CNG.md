C4Context
  title System Context diagram for null

System_Boundary(cpxxV9fxmR1pgCAxHFdB, "BAH CNG") {
    Component(BxsQud7gKwTvBJQ0KNj2, "Commercial Analysis Export", "")
    Component(oLiyCOrWiJ4p4jQxIr1F, "OpenSearch Ingestion", "")
    Component(heuHGHPQyPiOSCrreA29, "API Gateway", "")
    Component(p4yomm0RrOrYTNX8VvEg, "Direct Connect", "")
    Component(hQNUt4ZkKRpf1tU4WmD7, "Cache Deletion System", "")
    Component(LOCysP2vMe5DXLAHEYdU, "OpenSearch Dashboard", "")
    Component(Xoxj4IX90NjN8wiNvxFB, "Network Load Balancer", "")
    Component(OmsC5XSTv64h2ua9yNDK, "Solr API", "")
    Component(MoA8eTudOD7sWkzZF9av, "Transit Gateway", "")
    Component(uFk6F6ZcKKvnkVRkCNgH, "FireWall", "")
    Component(4teQrlbMUj5LLqxdVcPK, "BAHPCA API", "")
    ContainerDb(N5ViUhQIiCffQr67385p, "BAH CA - SQL Server", "Steve Edgerton:\n\n- BAHSQL1.bah.ba.com\n- CNG , schema dbo.\n- 100.64.64.215\n- Account:\nbahsql-prd (147441800008)\n- TCP 1433\n\nLive and Test users\n\nSystem:\n\nWindows Server 2019 ,SQL Server\n2017, Domain BAH.ba.com", "")
    ContainerDb(MzZVLTUQ74KbySVwdHfZ, "OpenSearch", "", "")
}


Person(wSyyLfkRhknshpX3pdWJ, "Internal Consumer", "")
Component(DzQ4VPgJYKxSkilz6snk, "BAHPCA_repo", "")
Component(6uyz5GXgHFSVjrwIwRKC, "Common_Repo", "")
Container(DqaxYHtWGYqlWeYgZkyP, "Common Lib", "", "")
System(LqXZYPgQ9UPjc7lqtp7S, "Akamai", "")
Component(nd7uOWs4bfaeXm73vKFa, "Solr_Repo", "")
Container(JOCj92Vc2CEAUN8hquBI, "Spring Framework", "", "")
Container(Vg8nvjzPDirSBRjT8K3Q, "Solr API Code", "", "")
Container(Nr2rq32hBezwoAGeNT2U, "BAHPCA API Code", "", "")
Component(FbHUk96QaZQVUumba63H, "Batch Controller", "")
Component(F2hUPV68esNdYOZlEDZp, "Git Pipeline", "")
Person(mtyHirRmDImFDmca6URg, "Cache Admin", "")
Person(f1fiXJziytRGVe7hFuLo, "Commercial Analyst", "")


Rel(MzZVLTUQ74KbySVwdHfZ, Vg8nvjzPDirSBRjT8K3Q, "reads data")
Rel(heuHGHPQyPiOSCrreA29, 4teQrlbMUj5LLqxdVcPK, "Accesses API implementations")
Rel(LqXZYPgQ9UPjc7lqtp7S, heuHGHPQyPiOSCrreA29, "Access  secured systems")
Rel(MzZVLTUQ74KbySVwdHfZ, Nr2rq32hBezwoAGeNT2U, "reads data")
Rel(MzZVLTUQ74KbySVwdHfZ, LOCysP2vMe5DXLAHEYdU, "Retrieve Cache")
Rel(wSyyLfkRhknshpX3pdWJ, Xoxj4IX90NjN8wiNvxFB, "Request Cache Data")
Rel(F2hUPV68esNdYOZlEDZp, 4teQrlbMUj5LLqxdVcPK, "Deploy")
Rel(p4yomm0RrOrYTNX8VvEg, MoA8eTudOD7sWkzZF9av, "Flow")
Rel(FbHUk96QaZQVUumba63H, p4yomm0RrOrYTNX8VvEg, "OnPrem / AWS connectivity")
Rel(uFk6F6ZcKKvnkVRkCNgH, N5ViUhQIiCffQr67385p, "Access SQL Server")
Rel(N5ViUhQIiCffQr67385p, uFk6F6ZcKKvnkVRkCNgH, "return Access SQL Server")
Rel(BxsQud7gKwTvBJQ0KNj2, uFk6F6ZcKKvnkVRkCNgH, "Create Commercial Analysis Data")
Rel(oLiyCOrWiJ4p4jQxIr1F, MzZVLTUQ74KbySVwdHfZ, "Store Offers")
Rel(heuHGHPQyPiOSCrreA29, Vg8nvjzPDirSBRjT8K3Q, "Accesses API implementations")
Rel(uFk6F6ZcKKvnkVRkCNgH, f1fiXJziytRGVe7hFuLo, "Analyse Commercial Data")
Rel(LOCysP2vMe5DXLAHEYdU, mtyHirRmDImFDmca6URg, "Visualize Cache")
Rel(F2hUPV68esNdYOZlEDZp, OmsC5XSTv64h2ua9yNDK, "Deploy")
Rel(MzZVLTUQ74KbySVwdHfZ, BxsQud7gKwTvBJQ0KNj2, "Retrieve Cache")
Rel(MoA8eTudOD7sWkzZF9av, oLiyCOrWiJ4p4jQxIr1F, "Flow")
Rel(F2hUPV68esNdYOZlEDZp, BxsQud7gKwTvBJQ0KNj2, "Deploy")
Rel(hQNUt4ZkKRpf1tU4WmD7, MzZVLTUQ74KbySVwdHfZ, "Delete Cache Records")


  UpdateLayoutConfig($c4ShapeInRow="5", $c4BoundaryInRow="1")
