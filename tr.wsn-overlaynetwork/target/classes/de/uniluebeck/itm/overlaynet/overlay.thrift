# overlay.thrift
	namespace java de.uniluebeck.itm.overlaynet
	
	struct Metadata {
		1:i64 id,
		2:string fabricate,
		3:string osversion,
		4:string ipadress,
	}
	

	service OverlayServer {
		i64 add(1:Metadata data),
		list<Metadata> search(1:i64 id),
		i64 remove(1:i64 id)
	}