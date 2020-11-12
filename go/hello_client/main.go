package main

import (
	"context"
	"github.com/making/demo-grpc/go/hello_client/demo"
	"google.golang.org/grpc"
	"log"
	"time"
)

func main() {
	conn, err := grpc.Dial("localhost:50051", grpc.WithInsecure(), grpc.WithBlock())
	if err != nil {
		log.Fatalf("did not connect: %v", err)
	}
	defer conn.Close()
	client := demo.NewHelloServiceClient(conn)
	ctx, cancel := context.WithTimeout(context.Background(), time.Second)
	defer cancel()
	response, err := client.SayHello(ctx, &demo.HelloRequest{Greeting: "World"})
	if err != nil {
		log.Fatalf("could not say hello: %v", err)
	}
	log.Printf("%s", response.Reply)
}
