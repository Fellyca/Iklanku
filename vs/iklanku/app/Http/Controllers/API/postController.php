<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;

use Illuminate\Http\Request;
use App\Models\post;

class postController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $post = post::all();
        if($post){
            return response()->json([
                'status'=>200,
                'message'=>'Data berhasil ditampilkan',
                'data'=> $post
            ]);
        } else {
            return response()->json([
                'status'=>400,
                'message'=>'Data gagal ditampilkan'
            ]);
        }
        
    }

   
    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        try {
            $post = new post;
            $post->image = $request->image;
            $post->judul = $request->judul;
            $post->user_id = $request->user_id;
            $post->save();
            return response()->json([
                'status'=>200,
                'message'=>'Data berhasil ditambah',
                'data'=> $post
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'status'=>400,
                'message'=>'Data gagal ditambah'
            ]);
        }
        
        
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        $post = post::findOrFail($id);
        if($post){
            return response()->json([
                'status'=>200,
                'message'=>'Data berhasil ditambah',
                'data'=> $post
            ]);
        } else {
            return response()->json([
                'status'=>400,
                'message'=>'Data gagal ditampilkan'
            ]);
        }
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        try {
            $post = post::findOrFail($id);
            $post->image = $request->image;
            $post->judul = $request->judul;
            $post->save();
            return response()->json([
                'status'=>200,
                'message'=>'Data berhasil diubah',
                'data'=> $post
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'status'=>400,
                'message'=>'Data gagal diubah'
            ]);
        }
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        $post = post::find($id);
        
        if ($post) {
            $post->delete();
            return response()->json([
                'status'=>200,
                'message'=>'Data berhasil dihapus'
            ]);
        } else {
            return response()->json([
                'status'=>400,
                'message'=>'Data gagal dihapus'
            ]);
        }
        

    }
}
